package com.svape.masterunalapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svape.masterunalapp.data.model.HechoDelictivo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val hechos: List<HechoDelictivo>) : UiState()
    data class Error(val message: String) : UiState()
}

class DataCatalogViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val baseUrl = "https://www.datos.gov.co/resource/4rxi-8m8d.json"

    fun fetchHechosDelictivos(
        departamento: String = "",
        municipio: String = "",
        anio: String = "",
        limit: Int = 100
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val hechos = withContext(Dispatchers.IO) {
                    getHechosFromApi(departamento, municipio, anio, limit)
                }
                _uiState.value = UiState.Success(hechos)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private suspend fun getHechosFromApi(
        departamento: String,
        municipio: String,
        anio: String,
        limit: Int
    ): List<HechoDelictivo> = withContext(Dispatchers.IO) {
        val urlBuilder = StringBuilder(baseUrl)

        val params = mutableListOf<String>()

        if (departamento.isNotEmpty()) {
            val deptoNorm = normalizarTexto(departamento)
            params.add("departamento=$deptoNorm")
        }

        if (municipio.isNotEmpty()) {
            val muniNorm = normalizarTexto(municipio)
            params.add("municipio=$muniNorm")
        }

        if (anio.isNotEmpty() && anio.matches(Regex("\\d{4}"))) {
            params.add("\$where=starts_with(fecha_hecho, '$anio')")
        }

        params.add("\$limit=$limit")

        params.add("\$order=fecha_hecho DESC")

        if (params.isNotEmpty()) {
            urlBuilder.append("?")
            urlBuilder.append(params.joinToString("&"))
        }

        val finalUrl = urlBuilder.toString()
        println("URL: $finalUrl")

        val url = URL(finalUrl)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "MasterUnalApp/1.0")

            val responseCode = connection.responseCode
            println("Código: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                println("Datos OK: ${response.take(100)}...")
                return@withContext parseJsonResponse(response)
            } else {
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
                    ?: "Sin detalles de error"
                println("Error $responseCode: $errorBody")
                throw Exception("Error al consultar datos. Código: $responseCode")
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun normalizarTexto(texto: String): String {
        val textoUpper = texto.uppercase()

        val reemplazos = mapOf(
            'Á' to 'A', 'É' to 'E', 'Í' to 'I', 'Ó' to 'O', 'Ú' to 'U',
            'Ñ' to 'N'
        )

        return textoUpper.map { char ->
            reemplazos[char] ?: char
        }.joinToString("")
    }

    private fun parseJsonResponse(jsonString: String): List<HechoDelictivo> {
        val hechos = mutableListOf<HechoDelictivo>()

        try {
            val jsonArray = org.json.JSONArray(jsonString)
            println("Registros: ${jsonArray.length()}")

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                val hecho = HechoDelictivo(
                    fechaHecho = obj.optString("fecha_hecho", "Sin fecha"),
                    codDepto = obj.optString("cod_depto", ""),
                    departamento = obj.optString("departamento", "Sin departamento"),
                    codMuni = obj.optString("cod_muni", ""),
                    municipio = obj.optString("municipio", "Sin municipio"),
                    cantidad = obj.optString("cantidad", "0")
                )

                hechos.add(hecho)
            }
        } catch (e: Exception) {
            println("Error JSON: ${e.message}")
            throw Exception("Error al procesar datos: ${e.message}")
        }

        return hechos
    }
}