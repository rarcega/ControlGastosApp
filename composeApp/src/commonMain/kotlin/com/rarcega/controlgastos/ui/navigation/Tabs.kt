package com.rarcega.controlgastos.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.rarcega.controlgastos.ui.dashboard.DashboardScreen
import com.rarcega.controlgastos.ui.transactions.TransactionsScreen
import com.rarcega.controlgastos.ui.accounts.AccountsScreen
import com.rarcega.controlgastos.ui.budget.BudgetScreen
import com.rarcega.controlgastos.ui.settings.SettingsScreen

object DashboardTab : Tab {
    private fun readResolve(): Any = DashboardTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Dashboard)
            return TabOptions(
                index = 0u,
                title = "Dashboard",
                icon = icon
            )
        }

    @Composable
    override fun Content() {
        DashboardScreen()
    }
}

object TransactionsTab : Tab {
    private fun readResolve(): Any = TransactionsTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.ListAlt)
            return TabOptions(
                index = 1u,
                title = "Movimientos",
                icon = icon
            )
        }

    @Composable
    override fun Content() {
        TransactionsScreen()
    }
}

object AccountsTab : Tab {
    private fun readResolve(): Any = AccountsTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.AccountBalance)
            return TabOptions(
                index = 2u,
                title = "Cuentas",
                icon = icon
            )
        }

    @Composable
    override fun Content() {
        AccountsScreen()
    }
}

object BudgetTab : Tab {
    private fun readResolve(): Any = BudgetTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.BarChart)
            return TabOptions(
                index = 3u,
                title = "Presupuesto",
                icon = icon
            )
        }

    @Composable
    override fun Content() {
        BudgetScreen()
    }
}

object SettingsTab : Tab {
    private fun readResolve(): Any = SettingsTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Settings)
            return TabOptions(
                index = 4u,
                title = "Ajustes",
                icon = icon
            )
        }

    @Composable
    override fun Content() {
        SettingsScreen()
    }
}
