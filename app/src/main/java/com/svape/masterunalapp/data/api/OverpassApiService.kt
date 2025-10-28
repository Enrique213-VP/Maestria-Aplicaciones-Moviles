package com.svape.masterunalapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName

data class OverpassResponse(
    val elements: List<Element>
)

data class Element(
    val type: String,
    val id: Long,
    val lat: Double?,
    val lon: Double?,
    val tags: Tags?
)

data class Tags(
    val name: String?,
    val amenity: String?,
    val tourism: String?,
    val shop: String?,
    @SerializedName("addr:street")
    val street: String?,
    @SerializedName("addr:housenumber")
    val houseNumber: String?,
    val website: String?,
    val phone: String?,
    val opening_hours: String?
)

interface OverpassApiService {
    @GET("api/interpreter")
    suspend fun getNearbyPOIs(
        @Query("data") query: String
    ): OverpassResponse

    companion object {
        private const val BASE_URL = "https://overpass-api.de/"

        fun create(): OverpassApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(OverpassApiService::class.java)
        }
    }
}

object OverpassQueryBuilder {
    fun buildPOIQuery(
        lat: Double,
        lon: Double,
        radiusKm: Float,
        categories: List<String> = listOf("amenity", "tourism", "shop")
    ): String {
        val radiusMeters = (radiusKm * 1000).toInt()
        val categoriesQuery = categories.joinToString("") { category ->
            "node[$category](around:$radiusMeters,$lat,$lon);"
        }

        return """
            [out:json][timeout:25];
            (
                $categoriesQuery
            );
            out body;
            >;
            out skel qt;
        """.trimIndent()
    }

    fun buildSpecificPOIQuery(
        lat: Double,
        lon: Double,
        radiusKm: Float,
        poiType: String,
        poiValue: String
    ): String {
        val radiusMeters = (radiusKm * 1000).toInt()

        return """
            [out:json][timeout:25];
            (
                node["$poiType"="$poiValue"](around:$radiusMeters,$lat,$lon);
            );
            out body;
            >;
            out skel qt;
        """.trimIndent()
    }
}