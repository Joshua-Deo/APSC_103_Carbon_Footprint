package com.example.carbonfootprintstuff

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class ApiEntry(
    val id: Int,
    val user_name: String,
    val category: String,
    val activity: String,
    val entry_date: String,
    val carbon_kg_co2e: Float,
    val notes: String,
    val created_at: String
)

data class EntryResponse(val entries: List<ApiEntry>)
data class TotalResponse(val total_kg: Float)

data class EntryRequest(
    val user_name: String,
    val category: String,
    val activity: String,
    val carbon_kg_co2e: Float,
    val notes: String = ""
)

interface CarbonApi {
    @GET("entries")
    suspend fun getEntries(): EntryResponse

    @POST("entries")
    suspend fun addEntry(@Body entry: EntryRequest): Map<String, String>

    @GET("total")
    suspend fun getTotal(): TotalResponse
}

val api = Retrofit.Builder()
    .baseUrl("http://172.20.10.3:8000/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(CarbonApi::class.java)
