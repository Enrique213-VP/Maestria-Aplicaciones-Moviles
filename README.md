# 🤖 UNAL AI Assistant

Aplicación de chat con **Gemini 2.5 Flash** para estudiantes de la Universidad Nacional de Colombia.

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Compose-UI-green)
![Gemini](https://img.shields.io/badge/Gemini-2.5%20Flash-blue)

---

## ✨ Características

- 💬 Chat en tiempo real con Gemini AI
- 🎨 Material Design 3
- 🔄 Indicadores de carga
- 📱 Arquitectura MVVM
- 🗑️ Limpiar historial

---

## 🚀 Instalación Rápida

### 1. Obtener API Key
Ve a [Google AI Studio](https://aistudio.google.com/app/apikey) y crea tu API Key.

### 2. Configurar `local.properties`
```properties
GEMINI_API_KEY=TU_API_KEY_AQUI
```

### 3. Sync y Run
```bash
File → Sync Project with Gradle Files
Build → Rebuild Project
Run App
```

---

## 📦 Dependencias
```kotlin
// Gemini AI
implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
```

---

## 🛠 Estructura
```
├── ui/view/MainActivity.kt          # UI con Compose
├── ui/viewmodel/ChatViewModel.kt    # Lógica + Gemini AI
└── ui/theme/Theme.kt                # Material 3
```

---

## 🤖 Configuración del Modelo
```kotlin
private val generativeModel = GenerativeModel(
    modelName = "gemini-2.5-flash",
    apiKey = BuildConfig.GEMINI_API_KEY,
    generationConfig = generationConfig {
        temperature = 0.7f
        maxOutputTokens = 2048
    }
)
```

---

## 🐛 Errores Comunes

| Error | Solución |
|-------|----------|
| API Key inválida | Verifica `local.properties` |
| Modelo no encontrado (404) | Usa `gemini-2.5-flash` |
| Límite alcanzado (429) | Espera o verifica cuota |
| Sin conexión | Verifica internet y permisos |

---

## 🔑 Modelos Disponibles
```kotlin
"gemini-2.5-flash"        // ✅ Recomendado - Rápido
"gemini-2.5-pro"          // Más potente
"gemini-flash-latest"     // Última versión
```

---

## 📱 Uso
```kotlin
// Enviar mensaje
viewModel.sendMessage("Explica qué son las coroutines en Kotlin")

// Limpiar chat
viewModel.clearChat()

// Limpiar error
viewModel.clearError()
```

---

## ⚙️ build.gradle.kts
```kotlin
android {
    defaultConfig {
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }
        
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${localProperties.getProperty("GEMINI_API_KEY", "")}\""
        )
    }
    
    buildFeatures {
        buildConfig = true
    }
}
```

---

## 📝 AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 👨‍💻 Autor

**Universidad Nacional de Colombia**  
Maestría en Ingeniería de Software  
Curso: Desarrollo Móvil

---

## 🔗 Enlaces

- [Google AI Studio](https://aistudio.google.com)
- [Documentación Gemini](https://ai.google.dev/docs)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)

---

**Desarrollado con ❤️ en la UNAL** 🇨🇴
