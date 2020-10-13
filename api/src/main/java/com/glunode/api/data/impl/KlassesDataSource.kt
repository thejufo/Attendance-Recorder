package com.glunode.api.data.impl

import android.util.Log
import com.glunode.api.Klass
import com.glunode.api.ServiceLocator
import com.glunode.api.awaitRealtime
import com.glunode.api.data.DataSource
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

internal class KlassesDataSource(private val serviceLocator: ServiceLocator) : DataSource<Klass> {

    override suspend fun add(model: Klass): Boolean {
        return try {
            val ref = serviceLocator.klassesReference
            ref.child(model.uid.toString()).setValue(model).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun get(clause: Any): Klass? {
        return try {
            val ref = serviceLocator.klassesReference
            return ref.child(clause.toString()).awaitRealtime().packet?.getValue(Klass::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun update(model: Klass) = false

    override suspend fun delete(model: Klass) = try {
        serviceLocator.provideStudentsDataSource.getAllWhere(model.uid).forEach {
            ServiceLocator.provideStudentsDataSource.delete(it)
        }

        val klassRef = serviceLocator.klassesReference
        klassRef.child(model.uid.toString()).removeValue().await()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    override suspend fun getAll(): List<Klass> {
        val result = serviceLocator.klassesReference.awaitRealtime()
        return result.packet!!.children.map { it.getValue(Klass::class.java)!! }
    }

    override suspend fun deleteAll(): Boolean {
        TODO("Not yet implemented")
    }
}