package com.svape.masterunalapp.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.svape.masterunalapp.data.model.Company
import com.svape.masterunalapp.data.model.CompanyClassification

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "CompanyDirectory.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_COMPANIES = "companies"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_WEBSITE = "website"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PRODUCTS_SERVICES = "products_services"
        private const val COLUMN_CLASSIFICATION = "classification"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_COMPANIES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_WEBSITE TEXT,
                $COLUMN_PHONE TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PRODUCTS_SERVICES TEXT,
                $COLUMN_CLASSIFICATION TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTable)

        insertSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMPANIES")
        onCreate(db)
    }

    private fun insertSampleData(db: SQLiteDatabase) {
        val companies = listOf(
            Company(0, "Tech Solutions", "https://techsolutions.com", "3001234567",
                "info@techsolutions.com", "Desarrollo web, apps móviles",
                CompanyClassification.DESARROLLO_MEDIDA),
            Company(0, "Software Factory Inc", "https://softwarefactory.com", "3007654321",
                "contact@softwarefactory.com", "Desarrollo de software empresarial",
                CompanyClassification.FABRICA_SOFTWARE),
            Company(0, "Consultores TI", "https://consultoresti.com", "3009876543",
                "consultas@consultoresti.com", "Asesoría en transformación digital",
                CompanyClassification.CONSULTORIA)
        )

        companies.forEach { company ->
            val values = ContentValues().apply {
                put(COLUMN_NAME, company.name)
                put(COLUMN_WEBSITE, company.website)
                put(COLUMN_PHONE, company.phone)
                put(COLUMN_EMAIL, company.email)
                put(COLUMN_PRODUCTS_SERVICES, company.productsServices)
                put(COLUMN_CLASSIFICATION, company.classification.name)
            }
            db.insert(TABLE_COMPANIES, null, values)
        }
    }

    fun insertCompany(company: Company): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, company.name)
            put(COLUMN_WEBSITE, company.website)
            put(COLUMN_PHONE, company.phone)
            put(COLUMN_EMAIL, company.email)
            put(COLUMN_PRODUCTS_SERVICES, company.productsServices)
            put(COLUMN_CLASSIFICATION, company.classification.name)
        }

        return db.insert(TABLE_COMPANIES, null, values)
    }

    fun getAllCompanies(): List<Company> {
        val companies = mutableListOf<Company>()
        val db = readableDatabase
        val cursor: Cursor? = db.query(
            TABLE_COMPANIES, null, null, null, null, null, "$COLUMN_NAME ASC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    companies.add(cursorToCompany(it))
                } while (it.moveToNext())
            }
        }

        return companies
    }

    fun getCompanyById(id: Int): Company? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_COMPANIES, null, "$COLUMN_ID = ?", arrayOf(id.toString()),
            null, null, null
        )

        var company: Company? = null
        cursor?.use {
            if (it.moveToFirst()) {
                company = cursorToCompany(it)
            }
        }

        return company
    }

    fun searchCompanies(name: String? = null, classification: CompanyClassification? = null): List<Company> {
        val companies = mutableListOf<Company>()
        val db = readableDatabase

        val selection = mutableListOf<String>()
        val selectionArgs = mutableListOf<String>()

        name?.takeIf { it.isNotBlank() }?.let {
            selection.add("$COLUMN_NAME LIKE ?")
            selectionArgs.add("%$it%")
        }

        classification?.let {
            selection.add("$COLUMN_CLASSIFICATION = ?")
            selectionArgs.add(it.name)
        }

        val whereClause = if (selection.isEmpty()) null else selection.joinToString(" AND ")
        val whereArgs = if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray()

        val cursor = db.query(
            TABLE_COMPANIES, null, whereClause, whereArgs, null, null, "$COLUMN_NAME ASC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    companies.add(cursorToCompany(it))
                } while (it.moveToNext())
            }
        }

        return companies
    }

    fun updateCompany(company: Company): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, company.name)
            put(COLUMN_WEBSITE, company.website)
            put(COLUMN_PHONE, company.phone)
            put(COLUMN_EMAIL, company.email)
            put(COLUMN_PRODUCTS_SERVICES, company.productsServices)
            put(COLUMN_CLASSIFICATION, company.classification.name)
        }

        return db.update(
            TABLE_COMPANIES, values, "$COLUMN_ID = ?", arrayOf(company.id.toString())
        )
    }

    fun deleteCompany(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_COMPANIES, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    private fun cursorToCompany(cursor: Cursor): Company {
        return Company(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
            website = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEBSITE)) ?: "",
            phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)) ?: "",
            email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)) ?: "",
            productsServices = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTS_SERVICES)) ?: "",
            classification = CompanyClassification.fromString(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASSIFICATION))
            )
        )
    }
}