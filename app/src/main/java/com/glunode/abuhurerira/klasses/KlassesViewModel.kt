// Created by abdif on 8/2/2020

package com.glunode.abuhurerira.klasses

import androidx.lifecycle.*
import com.glunode.abuhurerira.minusAssign
import com.glunode.abuhurerira.plusAssign
import com.glunode.abuhurerira.update
import com.glunode.api.Klass
import com.glunode.api.Student
import com.glunode.api.data.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KlassesViewModel constructor(private val repo: AppRepository) : ViewModel() {

    private val _klasses = MutableLiveData<MutableList<Klass>>()
    val klasses: LiveData<List<Klass>>
        get() = _klasses.map { it.toList() }

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    val empty = Transformations.map(_klasses) {
        it.isEmpty()
    }

    init {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            _klasses.postValue(repo.getAllKlasses().toMutableList())
            _loading.postValue(false)
        }
    }

    fun addKlass(klass: Klass) {
        viewModelScope.launch {
            repo.addKlass(klass)
            _klasses += klass
        }
    }

    fun deleteKlass(klass: Klass) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            if (repo.deleteKlass(klass)) {
                withContext(Dispatchers.Main) {
                    _klasses -= klass
                    _loading.postValue(false)
                }
            }
        }
    }

    fun addStudents(vararg students: Student, callback: () -> Unit) {
        addStudents(students.toList(), callback)
    }


    fun addStudents(students: List<Student>, callback: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                students.forEach {
                    delay(500)
                    it.uid = System.currentTimeMillis()
                    repo.addStudent(it)
                }
                withContext(Dispatchers.Main) {
                    callback()
                }
            }
        }
    }

    fun renameKlass(klass: Klass, name: String) {
        viewModelScope.launch {
            klass.name = name
            if(repo.updateKlass(klass)) {
                val list = _klasses.value
                list?.update(klass)
                _klasses.value = list
            }
        }
    }

    class Factory(private val dataSource: AppRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return KlassesViewModel(dataSource) as T
        }
    }
}