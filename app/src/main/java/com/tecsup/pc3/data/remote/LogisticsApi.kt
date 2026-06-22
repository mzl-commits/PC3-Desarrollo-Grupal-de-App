package com.tecsup.pc3.data.remote

import retrofit2.http.GET

data class PageResponse<T>(
    val count: Int = 0,
    val results: List<T> = emptyList(),
)

data class CarStatusResponse(
    val estado: String = "Sin conexión",
)

data class MovementResponse(
    val id_log: Int = 0,
    val estado_anterior: String = "",
    val estado_nuevo: String = "",
    val fecha_cambio: String = "",
)

interface LogisticsApi {
    @GET("api/carro/")
    suspend fun getCarStatus(): CarStatusResponse

    @GET("api/cajas/")
    suspend fun getBoxes(): PageResponse<Any>

    @GET("api/despachos/")
    suspend fun getShipments(): PageResponse<Any>

    @GET("api/historial/")
    suspend fun getHistory(): PageResponse<MovementResponse>
}
