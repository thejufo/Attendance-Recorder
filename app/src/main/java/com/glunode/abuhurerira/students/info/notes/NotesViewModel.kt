package com.glunode.abuhurerira.students.info.notes

import androidx.lifecycle.*
import com.glunode.abuhurerira.minusAssign
import com.glunode.abuhurerira.plusAssign
import com.glunode.abuhurerira.update
import com.glunode.api.Note
import com.glunode.api.data.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesViewModel(private val repo: AppRepository) : ViewModel() {

    private val _notes = MutableLiveData<MutableList<Note>>()
    val notes: LiveData<List<Note>>
        get() = _notes.map { it.toList() }

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    val empty = Transformations.map(_notes) {
        it.isEmpty()
    }

    fun loadNotes(uid: String) {
        _loading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = repo.getAllNotes(uid)
                _notes.postValue(result.toMutableList())
                _loading.postValue(false)
            }
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            repo.addNote(note)
            _notes += note
        }
    }

    fun modifyNote(note: Note) {
        viewModelScope.launch {
            val list = _notes.value
            list?.update(note)
            _notes.value = list
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repo.deleteNote(note)
            _notes -= note
        }
    }

    class Factory(private val dataSource: AppRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NotesViewModel(dataSource) as T
        }
    }
}