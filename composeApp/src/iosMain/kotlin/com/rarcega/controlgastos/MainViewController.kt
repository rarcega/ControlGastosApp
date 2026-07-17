package com.rarcega.controlgastos

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Control de Gastos"
    ) {
        App()
    }
}
