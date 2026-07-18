package com.rarcega.controlgastos.di

import com.rarcega.controlgastos.data.local.AppDatabase
import com.rarcega.controlgastos.data.local.CategoryDao
import com.rarcega.controlgastos.data.local.TransactionDao
import com.rarcega.controlgastos.data.local.AccountDao
import com.rarcega.controlgastos.data.local.BudgetDao
import com.rarcega.controlgastos.data.local.createDatabase
import com.rarcega.controlgastos.data.local.getDatabaseBuilder
import com.rarcega.controlgastos.data.remote.NordigenApi
import com.rarcega.controlgastos.data.repository.CategoryRepositoryImpl
import com.rarcega.controlgastos.data.repository.TransactionRepositoryImpl
import com.rarcega.controlgastos.data.repository.AccountRepositoryImpl
import com.rarcega.controlgastos.data.repository.BudgetRepositoryImpl
import com.rarcega.controlgastos.domain.repository.CategoryRepository
import com.rarcega.controlgastos.domain.repository.TransactionRepository
import com.rarcega.controlgastos.domain.repository.AccountRepository
import com.rarcega.controlgastos.domain.repository.BudgetRepository
import com.rarcega.controlgastos.ui.dashboard.DashboardViewModel
import com.rarcega.controlgastos.ui.transactions.TransactionsViewModel
import com.rarcega.controlgastos.ui.accounts.AccountsViewModel
import com.rarcega.controlgastos.ui.budget.BudgetViewModel
import com.rarcega.controlgastos.ui.settings.SettingsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module

val appModule = module {
    // Database
    single { 
        createDatabase(getDatabaseBuilder())
    }
    single<CategoryDao> { get<AppDatabase>().categoryDao() }
    single<TransactionDao> { get<AppDatabase>().transactionDao() }
    single<AccountDao> { get<AppDatabase>().accountDao() }
    single<BudgetDao> { get<AppDatabase>().budgetDao() }

    // HTTP Client
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                })
            }
        }
    }

    // Nordigen API
    single { NordigenApi(get(), "", "") }

    // Repositories
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<AccountRepository> { AccountRepositoryImpl(get(), get()) }
    single<BudgetRepository> { BudgetRepositoryImpl(get()) }

    // ViewModels
    single { DashboardViewModel(get(), get(), get()) }
    single { TransactionsViewModel(get(), get()) }
    single { AccountsViewModel(get()) }
    single { BudgetViewModel(get(), get()) }
    single { SettingsViewModel(get(), get(), get()) }
}
