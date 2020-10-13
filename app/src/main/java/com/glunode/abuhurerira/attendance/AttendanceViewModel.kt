package com.glunode.abuhurerira.attendance

import androidx.lifecycle.*
import com.glunode.abuhurerira.update
import com.glunode.api.Attendance
import com.glunode.api.Date
import com.glunode.api.Klass
import com.glunode.api.data.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AttendanceViewModel(private val repository: AppRepository) : ViewModel() {

    private val _attendances = MutableLiveData<MutableList<Attendance>>()
    val attendances: LiveData<List<Attendance>>
        get() = _attendances.map { it.toList() }

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    fun loadAttendances(klass: Klass, date: Date) {
        _loading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _attendances.postValue(repository.getAttendanceList(klass, date).toMutableList())
                _loading.postValue(false)
            }
        }
    }

    fun updateAttendance(attendance: Attendance) {
        viewModelScope.launch {
            try {
                val list = _attendances.value
                list?.update(attendance)
                if (attendance.status != null)
                    repository.addAttendance(attendance)
                else
                    repository.deleteAttendance(attendance)
                _attendances.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    class Factory(private val dataSource: AppRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AttendanceViewModel(dataSource) as T
        }
    }
}