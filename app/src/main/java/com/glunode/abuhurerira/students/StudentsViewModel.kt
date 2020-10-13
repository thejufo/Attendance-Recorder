package com.glunode.abuhurerira.students

import android.net.Uri
import androidx.lifecycle.*
import com.glunode.api.Student
import com.glunode.api.data.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentsViewModel(private val repo: AppRepository) : ViewModel() {

    private val _students = MutableLiveData<MutableList<Student>>()
    val students: LiveData<List<Student>>
        get() = _students.map { it.toList() }

    init {
        loadStudents()
    }

    private fun loadStudents(archives: Boolean = false) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (archives.not())
                    _students.postValue(repo.getAllStudents().toMutableList())
                else
                    _students.postValue(repo.getArchivedStudents().toMutableList())
            }
        }
    }

    fun refresh(archives: Boolean) {
        loadStudents(archives)
    }

    fun updateStudent(student: Student, callback: (bool: Boolean) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = repo.updateStudent(student)
                withContext(Dispatchers.Main) {
                    callback(result)
                }
            }
        }
    }

    fun deleteStudent(student: Student, callback: (deleted: Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repo.deleteStudent(student)
            callback(result)
        }
    }

    fun uploadImageToFirebaseStorage(
        selectedPhotoUri: Uri,
        student: String,
        callback: (downloadUrl: Uri) -> Unit
    ) {
        viewModelScope.launch {
            repo.uploadImageToFirebaseStorage(selectedPhotoUri, student, callback)
        }
    }

    fun deleteAvatarFromFirebase(intentStudent: Student) {
        viewModelScope.launch {
            repo.deleteAvatarFromFirebase(intentStudent)
        }
    }

    class Factory(private val dataSource: AppRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return StudentsViewModel(dataSource) as T
        }
    }
}