package com.svape.masterunalapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svape.masterunalapp.data.database.DatabaseHelper
import com.svape.masterunalapp.data.model.Company
import com.svape.masterunalapp.data.model.CompanyClassification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyViewModel(application: Application) : AndroidViewModel(application) {

    private val databaseHelper = DatabaseHelper(application)

    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> = _companies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCompanies()
    }

    fun loadCompanies() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val result = databaseHelper.getAllCompanies()
            _companies.value = result
            _isLoading.value = false
        }
    }

    fun searchCompanies(name: String? = null, classification: CompanyClassification? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val result = databaseHelper.searchCompanies(name, classification)
            _companies.value = result
            _isLoading.value = false
        }
    }

    fun insertCompany(company: Company, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = databaseHelper.insertCompany(company)
            launch(Dispatchers.Main) {
                onComplete(id > 0)
                if (id > 0) loadCompanies()
            }
        }
    }

    fun updateCompany(company: Company, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = databaseHelper.updateCompany(company)
            launch(Dispatchers.Main) {
                onComplete(result > 0)
                if (result > 0) loadCompanies()
            }
        }
    }

    fun deleteCompany(id: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = databaseHelper.deleteCompany(id)
            launch(Dispatchers.Main) {
                onComplete(result > 0)
                if (result > 0) loadCompanies()
            }
        }
    }

    public override fun onCleared() {
        super.onCleared()
        databaseHelper.close()
    }
}