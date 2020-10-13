// Created by abdif on 8/10/2020

package com.glunode.api.data.impl

import com.glunode.api.Note
import com.glunode.api.ServiceLocator
import com.glunode.api.awaitRealtime
import com.glunode.api.data.DataSource
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await

internal class NotesDataSource(private val serviceLocator: ServiceLocator) : DataSource<Note> {

    override suspend fun add(model: Note) = try {
        serviceLocator.notesReference.child(model.student.toString())
            .child(model.uid.toString()).setValue(model).await()
        true
    } catch (e: Exception) {
        false
    }

    override suspend fun get(clause: Any): Note? {
        TODO("Not yet implemented")
    }

    override suspend fun update(model: Note): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun delete(model: Note) = try {
        serviceLocator.notesReference.child(model.student.toString())
            .child(model.uid.toString())
            .removeValue().await()
        true
    } catch (e: Exception) {
        false
    }

    override suspend fun getAll(): List<Note> {
        TODO("Not yet implemented")
    }

    override suspend fun  getAllWhere(vararg clause: Any): List<Note> {
        val result = serviceLocator.notesReference.child(clause[0].toString())
            .awaitRealtime()
        return result.packet!!.children.map { it.getValue(Note::class.java)!! }
    }

    override suspend fun deleteAll(): Boolean {
        TODO("Not yet implemented")
    }
}