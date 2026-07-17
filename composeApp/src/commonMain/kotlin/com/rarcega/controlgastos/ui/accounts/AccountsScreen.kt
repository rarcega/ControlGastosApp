package com.rarcega.controlgastos.ui.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rarcega.controlgastos.domain.model.Account
import com.rarcega.controlgastos.domain.model.AccountType
import com.rarcega.controlgastos.ui.theme.ColorNegative
import com.rarcega.controlgastos.ui.theme.ColorPositive
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel = koinInject()
) {
    val accounts by viewModel.accounts.collectAsState()
    val financialPosition by viewModel.financialPosition.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Posición Financiera") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Bank Accounts
            item {
                Text(
                    text = "Cuentas Bancarias",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            val bankAccounts = accounts.filter { it.type == AccountType.BANK }
            if (bankAccounts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(
                            text = "No hay cuentas bancarias configuradas",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(bankAccounts) { account ->
                    AccountCard(account = account)
                }
            }

            // Other Assets
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Otros Activos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            val otherAssets = accounts.filter { it.type != AccountType.BANK && it.type != AccountType.PENSION }
            if (otherAssets.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(
                            text = "No hay otros activos configurados",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(otherAssets) { account ->
                    AccountCard(account = account)
                }
            }

            // Pension Plan
            val pension = accounts.find { it.type == AccountType.PENSION }
            if (pension != null) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Plan de Pensiones",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    AccountCard(account = pension)
                }
            }

            // Summary
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SummaryCard(
                    totalBankBalance = financialPosition.totalBankBalance,
                    totalOtherAssets = financialPosition.totalOtherAssets,
                    totalPension = financialPosition.totalPension,
                    balance = financialPosition.balance,
                    patrimony = financialPosition.patrimony
                )
            }
        }
    }
}

@Composable
fun AccountCard(account: Account) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (account.bank.isNotEmpty()) {
                    Text(
                        text = account.bank,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = account.type.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "%.2f €".format(account.balance),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (account.balance >= 0) ColorPositive else ColorNegative
            )
        }
    }
}

@Composable
fun SummaryCard(
    totalBankBalance: Double,
    totalOtherAssets: Double,
    totalPension: Double,
    balance: Double,
    patrimony: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumen Financiero",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow("Total Cuentas Bancarias", totalBankBalance)
            SummaryRow("Total Otros Activos", totalOtherAssets)
            SummaryRow("Plan de Pensiones", totalPension)

            Spacer(modifier = Modifier.height(8.dp))

            SummaryRow("Dinero Real", totalBankBalance + totalOtherAssets)
            SummaryRow("Préstamos", 0.0) // TODO: Get from data
            SummaryRow("Balance", balance, if (balance >= 0) ColorPositive else ColorNegative)

            Spacer(modifier = Modifier.height(8.dp))

            SummaryRow("PATRIMONIO", patrimony, MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    amount: Double,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (label == "PATRIMONIO") FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = "%.2f €".format(amount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
