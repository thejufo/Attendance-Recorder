// Created by abdif on 8/2/2020

package com.glunode.abuhurerira.klasses

import androidx.lifecycle.*
import com.glunode.abuhurerira.minusAssign
import com.glunode.abuhurerira.plusAssign
import com.glunode.abuhurerira.update
import com.glunode.api.Date
import com.glunode.api.Klass
import com.glunode.api.Report
import com.glunode.api.Student
import com.glunode.api.data.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportViewModel constructor(private val repo: AppRepository) : ViewModel() {

    private val _reports = MutableLiveData<List<Report>>()
    val reports: LiveData<List<Report>>
        get() = _reports.map { it.toList() }

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
//                _reports.postValue(repo.getReports().toMutableList())
            }
        }
    }

    fun loadReports(klass: Klass, date: Date) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _reports.postValue(repo.getReports(klass, date).toMutableList())
            }
        }
    }

    class Factory(private val dataSource: AppRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ReportViewModel(dataSource) as T
        }
    }
}