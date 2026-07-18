package com.rarcega.controlgastos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.rarcega.controlgastos.di.appModule
import com.rarcega.controlgastos.ui.navigation.AccountsTab
import com.rarcega.controlgastos.ui.navigation.BudgetTab
import com.rarcega.controlgastos.ui.navigation.DashboardTab
import com.rarcega.controlgastos.ui.navigation.SettingsTab
import com.rarcega.controlgastos.ui.navigation.TransactionsTab
import com.rarcega.controlgastos.ui.theme.ControlGastosTheme
import org.koin.compose.KoinApplication

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    NavigationBarItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let { painter ->
                Icon(painter = painter, contentDescription = tab.options.title)
            }
        },
        label = { Text(tab.options.title) }
    )
}

@Composable
fun App() {
    KoinApplication(
        application = {
            modules(appModule)
        }
    ) {
        ControlGastosTheme {
            TabNavigator(DashboardTab) {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            TabNavigationItem(DashboardTab)
                            TabNavigationItem(TransactionsTab)
                            TabNavigationItem(AccountsTab)
                            TabNavigationItem(BudgetTab)
                            TabNavigationItem(SettingsTab)
                        }
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        CurrentTab()
                    }
                }
            }
        }
    }
}
