package com.rarcega.controlgastos.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NordigenTokenResponse(
    @SerialName("access") val access: String,
    @SerialName("access_expires") val accessExpires: Int,
    @SerialName("refresh") val refresh: String,
    @SerialName("refresh_expires") val refreshExpires: Int
)

@Serializable
data class NordigenAccount(
    @SerialName("id") val id: String,
    @SerialName("created") val created: String? = null,
    @SerialName("last_accessed") val lastAccessed: String? = null,
    @SerialName("iban") val iban: String? = null,
    @SerialName("institution_id") val institutionId: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("owner_name") val ownerName: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("product") val product: String? = null,
    @SerialName("cash_account_type") val cashAccountType: String? = null,
    @SerialName("bic") val bic: String? = null,
    @SerialName("country") val country: String? = null
)

@Serializable
data class NordigenBalance(
    @SerialName("balanceAmount") val balanceAmount: NordigenAmount,
    @SerialName("balanceType") val balanceType: String
)

@Serializable
data class NordigenAmount(
    @SerialName("amount") val value: String,
    @SerialName("currency") val currency: String
)

@Serializable
data class NordigenTransaction(
    @SerialName("transactionId") val transactionId: String? = null,
    @SerialName("amount") val amount: NordigenAmount,
    @SerialName("bookingDate") val bookingDate: String? = null,
    @SerialName("valueDate") val valueDate: String? = null,
    @SerialName("remittanceInfo") val remittanceInfo: NordigenRemittanceInfo? = null,
    @SerialName("proprietaryBankTransactionCode") val proprietaryBankTransactionCode: String? = null,
    @SerialName("debtorName") val debtorName: String? = null,
    @SerialName("creditorName") val creditorName: String? = null,
    @SerialName("additionalInformation") val additionalInformation: String? = null
)

@Serializable
data class NordigenRemittanceInfo(
    @SerialName("unstructured") val unstructured: String? = null,
    @SerialName("structured") val structured: String? = null
)

@Serializable
data class NordigenTransactionsResponse(
    @SerialName("transactions") val transactions: NordigenTransactions? = null,
    @SerialName("balances") val balances: List<NordigenBalance>? = null
)

@Serializable
data class NordigenTransactions(
    @SerialName("booked") val booked: List<NordigenTransaction>? = null,
    @SerialName("pending") val pending: List<NordigenTransaction>? = null
)

@Serializable
data class NordigenInstitution(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("bic") val bic: String? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("logo") val logo: String? = null
)
