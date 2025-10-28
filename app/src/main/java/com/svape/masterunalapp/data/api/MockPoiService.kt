package com.svape.masterunalapp.data.api

import com.mapbox.geojson.Point
import com.svape.masterunalapp.ui.viewmodel.PointOfInterest
import kotlin.random.Random

object MockPoiService {

    fun generateMockPOIs(
        centerLat: Double,
        centerLon: Double,
        radiusKm: Float,
        types: Set<String>
    ): List<PointOfInterest> {
        val pois = mutableListOf<PointOfInterest>()
        var idCounter = 1L

        types.forEach { type ->
            val count = when(type) {
                "hospital" -> Random.nextInt(2, 5)
                "tourism" -> Random.nextInt(5, 10)
                "restaurant" -> Random.nextInt(10, 20)
                else -> Random.nextInt(3, 8)
            }

            repeat(count) {
                val poi = generateRandomPOI(
                    id = idCounter++,
                    type = type,
                    centerLat = centerLat,
                    centerLon = centerLon,
                    maxDistanceKm = radiusKm
                )
                pois.add(poi)
            }
        }

        return pois
    }

    private fun generateRandomPOI(
        id: Long,
        type: String,
        centerLat: Double,
        centerLon: Double,
        maxDistanceKm: Float
    ): PointOfInterest {
        val maxDegrees = maxDistanceKm / 111.0

        val angle = Random.nextDouble() * 2 * Math.PI
        val distance = Random.nextDouble() * maxDegrees

        val lat = centerLat + (distance * Math.sin(angle))
        val lon = centerLon + (distance * Math.cos(angle))

        val name = generateName(type, id)
        val address = generateAddress()

        return PointOfInterest(
            id = id,
            name = name,
            type = type,
            location = Point.fromLngLat(lon, lat),
            address = address,
            phone = if (Random.nextBoolean()) generatePhone() else null,
            website = if (Random.nextBoolean()) generateWebsite(name) else null,
            openingHours = if (type == "restaurant") "Lun-Vie: 12:00-22:00, Sáb-Dom: 11:00-23:00" else null
        )
    }

    private fun generateName(type: String, id: Long): String {
        return when(type) {
            "hospital" -> listOf(
                "Hospital San José",
                "Clínica del Country",
                "Hospital Universitario",
                "Centro Médico Colsubsidio",
                "Clínica Reina Sofía"
            ).random()
            "tourism" -> listOf(
                "Museo Nacional",
                "Plaza Bolívar",
                "Cerro de Monserrate",
                "Jardín Botánico",
                "Teatro Colón",
                "Museo del Oro",
                "Parque Simón Bolívar",
                "Catedral Primada"
            ).random()
            "restaurant" -> listOf(
                "Restaurante El Cielo",
                "Andrés Carne de Res",
                "La Puerta Falsa",
                "Casa Santa Clara",
                "Leo Cocina y Cava",
                "Harry Sasson",
                "Criterion",
                "Wok"
            ).random() + " #$id"
            else -> "Lugar $id"
        }
    }

    private fun generateAddress(): String {
        val calles = listOf("Calle", "Carrera", "Avenida", "Diagonal", "Transversal")
        val numeros = Random.nextInt(1, 200)
        val letra = if (Random.nextBoolean()) ('A'..'Z').random().toString() else ""
        val numero2 = Random.nextInt(1, 100)
        val numero3 = Random.nextInt(1, 50)

        return "${calles.random()} $numeros$letra # $numero2-$numero3"
    }

    private fun generatePhone(): String {
        val prefijo = listOf("601", "300", "301", "310", "311", "312", "313", "314", "315", "316", "317", "318", "319", "320", "321", "322", "323", "350", "351").random()
        val numero = Random.nextInt(1000000, 9999999)
        return "+57 $prefijo $numero"
    }

    private fun generateWebsite(name: String): String {
        val cleanName = name.lowercase()
            .replace(" ", "")
            .replace("#", "")
            .filter { it.isLetterOrDigit() }
        return "https://www.$cleanName.com.co"
    }
}