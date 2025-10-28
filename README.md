# Reto 9 – Accediendo al GPS

## Descripción del Proyecto
Este proyecto consiste en una aplicación Android desarrollada en **Kotlin** que obtiene la posición GPS actual del usuario (latitud y longitud) y muestra en un mapa los **puntos de interés (POI)** cercanos, tales como hospitales o lugares turísticos, dentro de un radio configurable expresado en kilómetros.

El mapa se implementa con **Mapbox**, y la comunicación con el servicio que entrega los POI se realiza mediante **OkHttp**. Para fines de demostración, la aplicación utiliza un **Mock de puntos de interés** (*Mock POI*) que simula una respuesta de servidor con datos de ubicación.

---

## Características Principales
- Obtención de la ubicación actual del usuario mediante GPS.  
- Visualización del mapa con **Mapbox SDK**.  
- Marcadores en el mapa que representan los puntos de interés cercanos.  
- Configuración del **radio de búsqueda** (en km) mediante preferencias de usuario.  
- Consumo de datos simulados con **OkHttp** y un **Mock POI** local.  
- Interfaz funcional, centrada en la posición actual del usuario.

---

## Tecnologías Utilizadas
- **Lenguaje:** Kotlin  
- **Mapa:** Mapbox Maps SDK for Android  
- **Red:** OkHttp  
- **Simulación de datos:** Mock POI (JSON local o endpoint simulado)  
- **Ubicación:** Android LocationManager / FusedLocationProvider  
- **Gestión de preferencias:** SharedPreferences  

---

## Funcionamiento
1. Al iniciar la aplicación, se solicita permiso para acceder a la ubicación del dispositivo.  
2. Se obtiene la latitud y longitud actuales mediante GPS.  
3. Se consulta el **Mock POI** a través de OkHttp para obtener una lista de lugares dentro del radio configurado.  
4. Se muestran los puntos de interés en el mapa, marcando su nombre y posición geográfica.  
5. El usuario puede cambiar el radio desde el menú de configuración, almacenado en las preferencias.

---

## Instalación y Ejecución
1. Clonar este repositorio en Android Studio.  
2. Agregar tu **token de Mapbox** en el archivo `local.properties`:
MAPBOX_DOWNLOADS_TOKEN=tu_token_aquí

markdown
Copy code
3. Sincronizar el proyecto con Gradle.  
4. Ejecutar la aplicación en un emulador o dispositivo con GPS habilitado.

---

## Próximas Mejoras
- Integrar una API real de puntos de interés (por ejemplo, Google Places o OpenStreetMap Nominatim).  
- Añadir filtrado por tipo de lugar (hospitales, restaurantes, etc.).  
- Implementar búsqueda por dirección textual.  
- Mejorar la interfaz gráfica con personalización de marcadores y capas del mapa.  

---

## Referencias
- [Documentación oficial de Mapbox](https://docs.mapbox.com/android/maps/overview/)  
- [OkHttp Library](https://square.github.io/okhttp/)  
- [API de ubicación de Android](https://developer.android.com/training/location)  
- [Repositorio osmdroid en GitHub](https://github.com/osmdroid/osmdroid/)  
