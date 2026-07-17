package com.rarcega.controlgastos

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.rarcega.controlgastos.di.appModule
import com.rarcega.controlgastos.ui.navigation.DashboardTab
import com.rarcega.controlgastos.ui.theme.ControlGastosTheme
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(
        application = {
            modules(appModule)
        }
    ) {
        ControlGastosTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                TabNavigator(DashboardTab) {
                    CurrentTab()
                }
            }
        }
    }
}
