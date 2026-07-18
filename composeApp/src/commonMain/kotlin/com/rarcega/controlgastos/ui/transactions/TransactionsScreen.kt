package com.rarcega.controlgastos.ui.transactions

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.Clock
import com.rarcega.controlgastos.domain.model.Category
import com.rarcega.controlgastos.domain.model.PaymentMethod
import com.rarcega.controlgastos.domain.model.Transaction
import com.rarcega.controlgastos.domain.model.TransactionType
import com.rarcega.controlgastos.ui.theme.ColorNegative
import com.rarcega.controlgastos.ui.theme.ColorPositive
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = koinInject()
) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movimientos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filtrar",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todos") },
                                onClick = {
                                    viewModel.setFilter(null)
                                    showFilterMenu = false
                                }
                            )
                            TransactionType.entries.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.displayName) },
                                    onClick = {
                                        viewModel.setFilter(type)
                                        showFilterMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar movimiento")
            }
        }
    ) { paddingValues ->
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay movimientos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        onDelete = { viewModel.deleteTransaction(transaction) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddTransactionDialog(
                categories = categories,
                onDismiss = { showAddDialog = false },
                onConfirm = { description, amount, type, paymentMethod, categoryId, categoryName, date, notes ->
                    viewModel.addTransaction(
                        description = description,
                        amount = amount,
                        type = type,
                        paymentMethod = paymentMethod,
                        categoryId = categoryId,
                        categoryName = categoryName,
                        date = date,
                        notes = notes
                    )
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun TransactionCard(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${transaction.categoryName} • ${transaction.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = transaction.paymentMethod.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${transaction.amount} €",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (transaction.type) {
                        TransactionType.INCOME -> ColorPositive
                        else -> ColorNegative
                    }
                )
                TextButton(onClick = onDelete) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    categories: List<Category>,
    onDismiss: () -> Unit,
    onConfirm: (
        String, Double, TransactionType, PaymentMethod,
        Long, String, kotlinx.datetime.LocalDate, String
    ) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE_VARIABLE) }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CARD) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Movimiento") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Importe") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Type selector
                Text("Tipo:", style = MaterialTheme.typography.bodyMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TransactionType.entries.forEach { type ->
                        TextButton(
                            onClick = { selectedType = type },
                            colors = if (selectedType == type) {
                                ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                ButtonDefaults.textButtonColors()
                            }
                        ) {
                            Text(type.displayName)
                        }
                    }
                }

                // Payment method selector
                Text("Método de pago:", style = MaterialTheme.typography.bodyMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMethod.entries.forEach { method ->
                        TextButton(
                            onClick = { selectedPaymentMethod = method },
                            colors = if (selectedPaymentMethod == method) {
                                ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                ButtonDefaults.textButtonColors()
                            }
                        ) {
                            Text(method.name)
                        }
                    }
                }

                // Category selector
                Text("Categoría:", style = MaterialTheme.typography.bodyMedium)
                LazyColumn(
                    modifier = Modifier.height(150.dp)
                ) {
                    items(categories) { category ->
                        DropdownMenuItem(
                            text = {
                                Text("${category.icon} ${category.name}")
                            },
                            onClick = { selectedCategory = category }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                    val today = kotlinx.datetime.Clock.System.now().toLocalDateTime(
                        kotlinx.datetime.TimeZone.currentSystemDefault()
                    ).date

                    onConfirm(
                        description,
                        parsedAmount,
                        selectedType,
                        selectedPaymentMethod,
                        selectedCategory?.id ?: 0L,
                        selectedCategory?.name ?: "",
                        today,
                        notes
                    )
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private object ButtonDefaults {
    @Composable
    fun textButtonColors(
        containerColor: Color = Color.Transparent
    ) = androidx.compose.material3.ButtonDefaults.textButtonColors(
        containerColor = containerColor
    )
}
