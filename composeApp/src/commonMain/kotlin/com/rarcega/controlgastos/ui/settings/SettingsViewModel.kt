package com.rarcega.controlgastos.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarcega.controlgastos.data.remote.NordigenApi
import com.rarcega.controlgastos.domain.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class NordigenConfig(
    val secretId: String = "",
    val secretKey: String = "",
    val isConnected: Boolean = false
)

class SettingsViewModel(
    private val nordigenApi: NordigenApi,
    private val accountRepository: AccountRepository,
    private val categoryRepository: com.rarcega.controlgastos.domain.repository.CategoryRepository
) : ViewModel() {

    private val _nordigenConfig = MutableStateFlow(NordigenConfig())
    val nordigenConfig: StateFlow<NordigenConfig> = _nordigenConfig

    init {
        // Initialize default categories
        viewModelScope.launch {
            categoryRepository.initializeDefaultCategories()
        }
    }

    fun saveNordigenConfig(secretId: String, secretKey: String) {
        _nordigenConfig.value = NordigenConfig(
            secretId = secretId,
            secretKey = secretKey,
            isConnected = secretId.isNotBlank() && secretKey.isNotBlank()
        )
    }

    fun syncNordigenAccounts() {
        viewModelScope.launch {
            try {
                accountRepository.syncNordigenAccounts()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
