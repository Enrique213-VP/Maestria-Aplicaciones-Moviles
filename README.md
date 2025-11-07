# Reto 10: Consumiendo Webservices del Catálogo de Datos Colombiano

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> Aplicación móvil Android que consume servicios web del Catálogo de Datos Abiertos de Colombia

---

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Características](#-características)
- [Tecnologías](#-tecnologías)
- [Dataset Utilizado](#-dataset-utilizado)
- [Arquitectura](#-arquitectura)
- [Capturas de Pantalla](#-capturas-de-pantalla)
- [Instalación](#-instalación)
- [Uso](#-uso)
- [API Reference](#-api-reference)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Requisitos Cumplidos](#-requisitos-cumplidos)
- [Autor](#-autor)

---

## 📱 Descripción

Aplicación móvil desarrollada en Android como parte del **Reto 10** del curso de Desarrollo de Aplicaciones para Dispositivos Móviles. La aplicación consume datos en tiempo real del **Catálogo de Datos Abiertos de Colombia** (datos.gov.co), específicamente el dataset de **Hechos Delictivos** del Ministerio de Defensa Nacional.

La aplicación permite a los usuarios consultar estadísticas de seguridad ciudadana filtradas por departamento, municipio y año, presentando la información de manera clara y organizada.

---

## ✨ Características

### Funcionalidades Principales

- ✅ **Consumo de API REST** en tiempo real del Catálogo de Datos Abiertos
- ✅ **Filtros inteligentes** mediante listas desplegables (dropdowns)
- ✅ **Búsqueda por:**
  - 📍 Departamento (33 departamentos de Colombia)
  - 🏙️ Municipio (ciudades principales, filtradas por departamento)
  - 📅 Año (desde 2003 hasta 2024)
- ✅ **Visualización de resultados** con tarjetas informativas
- ✅ **Código de colores** según cantidad de hechos:
  - 🟢 Verde: 1-5 hechos
  - 🟡 Amarillo: 6-10 hechos
  - 🔴 Rojo: Más de 10 hechos
- ✅ **Parseo de JSON** usando la API nativa de Android (`org.json`)
- ✅ **Manejo robusto de errores** y estados de carga
- ✅ **Interfaz moderna** con Material Design 3

### Características Técnicas

- 🏗️ **Arquitectura MVVM** (Model-View-ViewModel)
- 🎨 **Jetpack Compose** para UI declarativa
- ⚡ **Coroutines** para operaciones asíncronas
- 🔄 **StateFlow** para manejo reactivo de estado
- 🌐 **HttpURLConnection** para peticiones HTTP
- 📦 **Sealed Classes** para estados type-safe

---

## 🛠️ Tecnologías

| Tecnología | Uso |
|------------|-----|
| **Kotlin** | Lenguaje de programación principal |
| **Jetpack Compose** | Framework de UI moderno y declarativo |
| **Material Design 3** | Sistema de diseño y componentes UI |
| **Coroutines** | Programación asíncrona |
| **StateFlow** | Gestión reactiva de estado |
| **ViewModel** | Arquitectura MVVM |
| **HttpURLConnection** | Cliente HTTP nativo de Android |
| **org.json** | Parseo de JSON (SDK de Android) |

### Versiones

- **Kotlin:** 1.9.0
- **Compose BOM:** 2024.02.00
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Compile SDK:** 34

---

## 📊 Dataset Utilizado

### Información del Dataset

- **Nombre:** Hechos delictivos que afectan la seguridad ciudadana
- **Fuente:** Ministerio de Defensa Nacional de Colombia
- **Endpoint:** `https://www.datos.gov.co/resource/4rxi-8m8d.json`
- **Tipo:** API REST - Formato JSON
- **Actualización:** Periódica
- **Período:** 2003 - Actualidad

### Estructura de Datos

```json
{
  "fecha_hecho": "2024-01-01T00:00:00.000",
  "cod_depto": "11",
  "departamento": "BOGOTA D.C.",
  "cod_muni": "11001",
  "municipio": "BOGOTA",
  "cantidad": "15"
}
```

### Campos Disponibles

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `fecha_hecho` | String | Fecha del registro |
| `cod_depto` | String | Código del departamento |
| `departamento` | String | Nombre del departamento |
| `cod_muni` | String | Código del municipio |
| `municipio` | String | Nombre del municipio |
| `cantidad` | String | Cantidad de hechos registrados |

---

## 💻 Uso

### Realizar una Búsqueda

1. **Abrir la aplicación**
2. **Presionar** el botón "Consultar Datos Abiertos"
3. **Seleccionar filtros:**
   - Elegir un **departamento** del dropdown
   - (Opcional) Elegir un **municipio** específico
   - (Opcional) Elegir un **año**
4. **Presionar** el botón "Buscar"
5. **Ver resultados** en forma de tarjetas

### Ejemplos de Búsqueda

#### Búsqueda 1: Todos los hechos en Bogotá
```
Departamento: CUNDINAMARCA
Municipio: BOGOTA
Año: TODOS
```

#### Búsqueda 2: Antioquia en 2023
```
Departamento: ANTIOQUIA
Municipio: TODOS
Año: 2023
```

#### Búsqueda 3: Estadísticas del 2024
```
Departamento: TODOS
Municipio: TODOS
Año: 2024
```

### Limpiar Filtros

Presionar el botón **"Limpiar"** para resetear todos los filtros a "TODOS".

---

## 🔌 API Reference

### Endpoint Base

```
https://www.datos.gov.co/resource/4rxi-8m8d.json
```

### Parámetros de Consulta

| Parámetro | Tipo | Descripción | Ejemplo |
|-----------|------|-------------|---------|
| `departamento` | String | Filtrar por departamento | `departamento=CUNDINAMARCA` |
| `municipio` | String | Filtrar por municipio | `municipio=BOGOTA` |
| `$where` | String | Cláusula WHERE para filtros complejos | `$where=starts_with(fecha_hecho, '2023')` |
| `$limit` | Integer | Límite de registros | `$limit=100` |
| `$order` | String | Ordenar resultados | `$order=fecha_hecho DESC` |

### Ejemplo de Request

```http
GET https://www.datos.gov.co/resource/4rxi-8m8d.json?departamento=CUNDINAMARCA&municipio=BOGOTA&$limit=100&$order=fecha_hecho DESC
```

### Ejemplo de Response

```json
[
  {
    "fecha_hecho": "2023-01-01T00:00:00.000",
    "cod_depto": "11",
    "departamento": "BOGOTA D.C.",
    "cod_muni": "11001",
    "municipio": "BOGOTA",
    "cantidad": "15"
  }
]
```

---

## 📁 Estructura del Proyecto

```
app/
├── src/
│   └── main/
│       ├── java/com/svape/masterunalapp/
│       │   ├── data/
│       │   │   └── model/
│       │   │       └── HechoDelictivo.kt          # Modelo de datos
│       │   ├── ui/
│       │   │   ├── view/
│       │   │   │   ├── MainActivity.kt            # Pantalla de bienvenida
│       │   │   │   └── DataCatalogActivity.kt     # Pantalla principal
│       │   │   ├── viewmodel/
│       │   │   │   └── DataCatalogViewModel.kt    # Lógica de negocio
│       │   │   └── theme/
│       │   │       └── Theme.kt                   # Tema de la app
│       │   └── SplashActivity.kt                  # Splash screen
│       ├── AndroidManifest.xml                    # Configuración de la app
│       └── res/                                   # Recursos (drawables, strings, etc.)
└── build.gradle.kts                               # Dependencias
```

---

## ✅ Requisitos Cumplidos

### Requisitos Obligatorios del Reto 10

| Requisito | Estado | Implementación |
|-----------|--------|----------------|
| Consumir servicio web del Catálogo de Datos Colombiano | ✅ | `DataCatalogViewModel.kt` líneas 45-90 |
| Usar objeto JSON de Android SDK | ✅ | `org.json.JSONObject` líneas 110-135 |
| Interfaz gráfica para enviar parámetros | ✅ | Dropdowns en `DataCatalogActivity.kt` |
| Presentar respuesta del servicio web | ✅ | Tarjetas de resultados con toda la información |

### Extras Implementados

- ✅ **Arquitectura MVVM profesional**
- ✅ **Material Design 3** con Jetpack Compose
- ✅ **Listas desplegables (Dropdowns)** para mejor UX
- ✅ **Manejo de estados** (Idle, Loading, Success, Error)
- ✅ **Código de colores** para visualización rápida
- ✅ **Filtrado inteligente** de municipios por departamento
- ✅ **Normalización automática** de texto (elimina tildes)
- ✅ **Ordenamiento** de resultados por fecha
- ✅ **Responsive design** adaptable a diferentes pantallas

---

## 🎨 Diseño

### Paleta de Colores

```kotlin
Primary:       #1976D2  // Azul
OnPrimary:     #FFFFFF  // Blanco
Background:    #F5F5F5  // Gris claro
Surface:       #FFFFFF  // Blanco
Success:       #10B981  // Verde
Warning:       #F59E0B  // Amarillo
Error:         #EF4444  // Rojo
```

### Componentes UI

- **TopAppBar:** Color primario con título blanco
- **Dropdowns:** `ExposedDropdownMenuBox` de Material 3
- **Tarjetas:** `Card` con elevación de 2dp
- **Botones:** `Button` y `OutlinedButton`
- **Iconos:** Material Icons (LocationOn, CalendarToday, Search)

---

## 🧪 Testing

### Casos de Prueba

1. **Búsqueda sin filtros**
   - Todos = TODOS
   - Resultado: Primeros 100 registros

2. **Búsqueda por departamento**
   - Departamento = CUNDINAMARCA
   - Resultado: Todos los registros de Cundinamarca

3. **Búsqueda específica**
   - Departamento = ANTIOQUIA
   - Municipio = MEDELLIN
   - Año = 2023
   - Resultado: Registros filtrados correctamente

4. **Manejo de errores**
   - Sin conexión a internet
   - Resultado: Mensaje de error apropiado

---

## 🐛 Solución de Problemas

### Error: "Cannot resolve symbol HechoDelictivo"
**Solución:** Verificar que el archivo `HechoDelictivo.kt` esté en `data/model/`

### Error: "Unable to find explicit activity"
**Solución:** Verificar que `DataCatalogActivity` esté declarada en `AndroidManifest.xml`

### Error HTTP 400 o 404
**Solución:** 
- Verificar conexión a internet
- Revisar la URL del endpoint
- Verificar que el ViewModel use `DataCatalogViewModel_FIXED.kt`

### Los dropdowns no funcionan
**Solución:** 
- Verificar que se usa `DataCatalogActivity_DROPDOWN.kt`
- Hacer Sync Project y Rebuild

---

## 📚 Documentación Adicional

- [Datos Abiertos Colombia](https://www.datos.gov.co/)
- [Socrata API Docs](https://dev.socrata.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Material Design 3](https://m3.material.io/)

---

## 🎓 Autor

**Sergio Vargas Pedraza**

- Universidad Nacional de Colombia
- Curso: Desarrollo de Aplicaciones para Dispositivos Móviles
- Fecha: Noviembre 2024
- Reto: 10 - Consumiendo Webservices

---

## 📄 Licencia

Este proyecto fue desarrollado con fines académicos como parte del curso de Desarrollo de Aplicaciones para Dispositivos Móviles.

---

<div align="center">

**Hecho con Kotlin y Jetpack Compose**

⭐ Si te gustó este proyecto, dale una estrella en GitHub ⭐

</div>
