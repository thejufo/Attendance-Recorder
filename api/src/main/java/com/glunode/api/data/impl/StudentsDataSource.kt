// Created by abdif on 8/3/2020

package com.glunode.api.data.impl

import android.net.Uri
import android.util.Log
import com.glunode.api.*
import com.glunode.api.data.DataSource
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

internal class StudentsDataSource(private val serviceLocator: ServiceLocator) :
    DataSource<Student> {

    override suspend fun add(model: Student): Boolean {
        return try {
            val ref = serviceLocator.studentsReference
            val map = model.toHashMap()
            ref.child(model.uid.toString()).setValue(map).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun get(clause: Any): Student? {
        return try {
            val ref = serviceLocator.studentsReference
            ref.child(clause.toString()).awaitRealtime().packet?.toStudent()
//            with(serviceLocator.provideStudentsCollection) {
//                document(clause.toString()).get().await().toStudent()
//            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun update(model: Student): Boolean {
        return try {
            val ref = serviceLocator.studentsReference
            val map = model.toHashMap()
            ref.child(model.uid.toString()).setValue(map).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun delete(model: Student): Boolean {
        return try {
            Log.e("AAAAAA", "Performing student deletion")
            val attendanceDataSource = serviceLocator.provideAttendanceDataSource
            attendanceDataSource.getAllAttendancesForStudentForever(model.uid.toString()).forEach {
                attendanceDataSource.delete(it)
            }
            try {
                Firebase.storage.getReference("images").child(model.uid.toString()).delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            serviceLocator.studentsReference.child(model.uid.toString()).removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getAll() = getStudents()

    override suspend fun getAllWhere(vararg clause: Any): List<Student> {
        val klass = clause[0] as Klass?
        return if (klass != null) getStudentsByKlass(klass) else getStudents()
    }

    suspend fun getArchived(): List<Student> {
        val result = serviceLocator.studentsReference.awaitRealtime()
        return result.packet!!.children.map { it.toStudent() }
            .filter { it.archived }
    }

    private suspend fun getStudents(): List<Student> {
        val result = serviceLocator.studentsReference.awaitRealtime()
        return result.packet!!.children.map { it.toStudent1() }.filter { !it.archived }
    }

    private suspend fun getStudentsByKlass(klass: Klass): List<Student> {
        val result = serviceLocator.studentsReference.awaitRealtime()
        val students = result.packet!!.children
            .filter { it.child("klass").getValue(Long::class.java) == klass.uid }
            .filter { it.child("archived").getValue(Boolean::class.java) != true }
            .map { it.toStudent().also { it.klass = klass } }
            .sortedBy { it.no }
        return students
    }

    override suspend fun deleteAll(): Boolean {
        return true
    }

    fun uploadImageToFirebaseStorage(
        selectedPhotoUri: Uri,
        student: String,
        callback: (downloadUrl: Uri) -> Unit
    ) {
        val ref = Firebase.storage.getReference("/images/$student")
        ref.putFile(selectedPhotoUri)
            .addOnSuccessListener {
                @Suppress("NestedLambdaShadowedImplicitParameter")
                ref.downloadUrl.addOnSuccessListener {
                    callback(it)
                }
            }
    }

    suspend fun deleteAvatarFromFirebase(
        student: Student
    ) {
        val ref = Firebase.storage.getReference("/images/${student.uid}")
        try {
            ref.delete().await()
            student.avatar = null
            update(student)
        } catch (e: Exception) {

        }
    }
}
