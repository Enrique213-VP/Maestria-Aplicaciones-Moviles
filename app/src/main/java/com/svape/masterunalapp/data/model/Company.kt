package com.svape.masterunalapp.data.model

data class Company(
    val id: Int = 0,
    val name: String,
    val website: String,
    val phone: String,
    val email: String,
    val productsServices: String,
    val classification: CompanyClassification
)

enum class CompanyClassification(val displayName: String) {
    CONSULTORIA("Consultoría"),
    DESARROLLO_MEDIDA("Desarrollo a la medida"),
    FABRICA_SOFTWARE("Fábrica de software");

    companion object {
        fun fromString(value: String): CompanyClassification {
            return values().find { it.name == value } ?: CONSULTORIA
        }
    }
}