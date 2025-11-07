package com.svape.masterunalapp.data.model

data class PuntoPosconsumo(
    val nombre: String,
    val direccion: String,
    val ciudad: String,
    val departamento: String,
    val tipoResiduos: String,
    val telefono: String?,
    val latitud: Double?,
    val longitud: Double?
)

data class ApiResponse(
    val success: Boolean,
    val result: Result?
)

data class Result(
    val records: List<Record>
)

data class Record(
    val _id: Int,
    val NOMBRE: String?,
    val DIRECCION: String?,
    val CIUDAD: String?,
    val DEPARTAMENTO: String?,
    val TIPO_RESIDUO: String?,
    val TELEFONO: String?,
    val LATITUD: String?,
    val LONGITUD: String?
) {
    fun toPuntoPosconsumo(): PuntoPosconsumo {
        return PuntoPosconsumo(
            nombre = NOMBRE ?: "Sin nombre",
            direccion = DIRECCION ?: "Sin dirección",
            ciudad = CIUDAD ?: "Sin ciudad",
            departamento = DEPARTAMENTO ?: "Sin departamento",
            tipoResiduos = TIPO_RESIDUO ?: "No especificado",
            telefono = TELEFONO,
            latitud = LATITUD?.toDoubleOrNull(),
            longitud = LONGITUD?.toDoubleOrNull()
        )
    }
}