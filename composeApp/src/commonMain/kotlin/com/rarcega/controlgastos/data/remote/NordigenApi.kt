package com.rarcega.controlgastos.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.json

class NordigenApi(
    private val httpClient: HttpClient,
    private val secretId: String,
    private val secretKey: String
) {
    private val baseUrl = "https://ob.gocardless.com"
    private var accessToken: String? = null

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    suspend fun authenticate(): String {
        val response = httpClient.post("$baseUrl/api/v2/token/new/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("secret_id" to secretId, "secret_key" to secretKey))
        }.body<NordigenTokenResponse>()

        accessToken = response.access
        return response.access
    }

    suspend fun getAccounts(): List<NordigenAccount> {
        ensureAuthenticated()
        val response = httpClient.get("$baseUrl/api/v2/accounts/") {
            header("Authorization", "Bearer $accessToken")
        }.body<List<NordigenAccount>>()
        return response
    }

    suspend fun getBalances(accountId: String): List<NordigenBalance> {
        ensureAuthenticated()
        val response = httpClient.get("$baseUrl/api/v2/accounts/$accountId/balances/") {
            header("Authorization", "Bearer $accessToken")
        }.body<NordigenBalanceResponse>()
        return response.balances
    }

    suspend fun getTransactions(accountId: String, dateFrom: String? = null, dateTo: String? = null): NordigenTransactionsResponse {
        ensureAuthenticated()
        val response = httpClient.get("$baseUrl/api/v2/accounts/$accountId/transactions/") {
            header("Authorization", "Bearer $accessToken")
            dateFrom?.let { parameter("date_from", it) }
            dateTo?.let { parameter("date_to", it) }
        }.body<NordigenTransactionsResponse>()
        return response
    }

    suspend fun getInstitutions(country: String = "ES"): List<NordigenInstitution> {
        ensureAuthenticated()
        val response = httpClient.get("$baseUrl/api/v2/institutions/") {
            header("Authorization", "Bearer $accessToken")
            parameter("country", country)
        }.body<List<NordigenInstitution>>()
        return response
    }

    suspend fun createRequisition(institutionId: String, redirectUrl: String): NordigenRequisition {
        ensureAuthenticated()
        val response = httpClient.post("$baseUrl/api/v2/requisitions/") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $accessToken")
            setBody(mapOf(
                "institution_id" to institutionId,
                "redirect" to redirectUrl,
                "reference" to "controlgastos_${System.currentTimeMillis()}"
            ))
        }.body<NordigenRequisition>()
        return response
    }

    private suspend fun ensureAuthenticated() {
        if (accessToken == null) {
            authenticate()
        }
    }
}

@kotlinx.serialization.Serializable
data class NordigenBalanceResponse(
    @kotlinx.serialization.SerialName("balances") val balances: List<NordigenBalance>
)

@kotlinx.serialization.Serializable
data class NordigenRequisition(
    @kotlinx.serialization.SerialName("id") val id: String,
    @kotlinx.serialization.SerialName("link") val link: String? = null,
    @kotlinx.serialization.SerialName("status") val status: String? = null
)
