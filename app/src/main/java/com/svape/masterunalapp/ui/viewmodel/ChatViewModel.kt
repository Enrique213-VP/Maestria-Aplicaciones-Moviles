package com.svape.masterunalapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.svape.masterunalapp.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel : ViewModel() {

    private val TAG = "ChatViewModel"

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 8192
        }
    )

    init {
        Log.d(TAG, "ChatViewModel inicializado con Gemini 2.5 Flash")

        _uiState.value = _uiState.value.copy(
            messages = listOf(
                ChatMessage(
                    text = """
                        ¡Hola! Soy tu asistente de IA de la Universidad Nacional de Colombia.
                        
                        Estoy aquí para ayudarte con:
                        • Responder preguntas académicas
                        • Explicar conceptos de programación
                        • Ayudarte con tus proyectos de desarrollo móvil
                        • Resolver dudas sobre Kotlin y Android
                        
                        ¿En qué puedo ayudarte hoy?
                    """.trimIndent(),
                    isUser = false
                )
            )
        )
    }

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        Log.d(TAG, "Mensaje del usuario: $userMessage")

        val userChatMessage = ChatMessage(text = userMessage.trim(), isUser = true)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userChatMessage,
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            try {
                Log.d(TAG, "Enviando mensaje a Gemini...")

                val prompt = """
                    Eres un asistente académico de la Universidad Nacional de Colombia.
                    Ayudas a estudiantes de Ingeniería de Software, especialmente en desarrollo móvil con Kotlin y Android.
                    Responde en español de forma clara, práctica y amigable.
                    
                    Pregunta: $userMessage
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)

                Log.d(TAG, "Respuesta recibida")

                val aiMessage = ChatMessage(
                    text = response.text ?: "No pude generar una respuesta. Intenta de nuevo.",
                    isUser = false
                )

                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + aiMessage,
                    isLoading = false
                )

                Log.d(TAG, "Respuesta agregada al chat")

            } catch (e: Exception) {
                Log.e(TAG, "Error completo: ${e.message}", e)

                val errorMessage = when {
                    e.message?.contains("API_KEY_INVALID", ignoreCase = true) == true ->
                        "API Key inválida. Verifica tu configuración."
                    e.message?.contains("404", ignoreCase = true) == true ||
                            e.message?.contains("not found", ignoreCase = true) == true ->
                        "Modelo no disponible. Contacta al desarrollador."
                    e.message?.contains("quota", ignoreCase = true) == true ||
                            e.message?.contains("429", ignoreCase = true) == true ->
                        "Límite de uso alcanzado. Intenta más tarde."
                    e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true ||
                            e.message?.contains("403", ignoreCase = true) == true ->
                        "Permiso denegado. Verifica tu API Key."
                    e.message?.contains("network", ignoreCase = true) == true ||
                            e.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                        "Error de conexión. Verifica tu internet."
                    else ->
                        "Error: ${e.message ?: "Desconocido"}"
                }

                _uiState.value = _uiState.value.copy(
                    error = errorMessage,
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearChat() {
        Log.d(TAG, "Chat reiniciado")
        _uiState.value = ChatUiState(
            messages = listOf(
                ChatMessage(
                    text = "Chat reiniciado. ¿En qué puedo ayudarte?",
                    isUser = false
                )
            )
        )
    }
}