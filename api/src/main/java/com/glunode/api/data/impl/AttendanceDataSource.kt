// Created by abdif on 8/7/2020

package com.glunode.api.data.impl

import android.util.Log
import com.glunode.api.*
import com.glunode.api.data.DataSource
import kotlinx.coroutines.tasks.await

internal class AttendanceDataSource(private val serviceLocator: ServiceLocator) :
    DataSource<Attendance> {

    override suspend fun add(model: Attendance): Boolean {
        return addAtt(model)
//        val student = model.student?.uid.toString()
//        val date = "${model.date?.year}-${model.date?.month}-${model.date?.day}"
//        serviceLocator.provideStudentsCollection.document(student)
//            .collection("attendance").document(date)
//            .set(model.toHashMap()).await()
//        return true
    }

    private suspend fun addAtt(model: Attendance): Boolean {
        val monthYear = "${model.date?.year}-${model.date?.month}"
        val reference = serviceLocator.attendanceReference
        reference.child(monthYear).child("${model.date?.day}").child(model.student?.uid.toString())
            .setValue(model.toHashMap()).await()
        return true
    }

    suspend fun getAllAttendancesForStudentForever(uid: String): List<Attendance> {
        val reference = serviceLocator.attendanceReference
        val result = reference.awaitRealtime()
        val list = mutableListOf<Attendance>()
        result.packet?.children?.forEach {
            it.children.forEach {
                it.children.filter { it.key == uid }.forEach {
                    it.toAttendance()?.let {
                        list.add(it)
                    }
                }
            }
        }
        return list
    }

    suspend fun getAllAttendancesForStudentPerYearMonth(
        uid: String,
        monthYear: String
    ): List<Attendance> {
        val reference = serviceLocator.attendanceReference
        val result = reference.child(monthYear).awaitRealtime()
        val list = mutableListOf<Attendance>()
        result.packet?.children?.map {
            it.children.filter { it.key == uid }.forEach {
                it.toAttendance1()?.let {
                    list.add(it)
                }
            }
        }
        return list
    }

    override suspend fun get(clause: Any): Attendance? {
        TODO("Not yet implemented")
    }

    override suspend fun update(model: Attendance): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun delete(model: Attendance): Boolean {
        Log.e("AAAAAA", "deleteing aattendance")
        return deleteAtt(model)
//        val student = model.student?.uid.toString()
//        val date = "${model.date?.year}-${model.date?.month}-${model.date?.day}"
//        serviceLocator.provideStudentsCollection.document(student)
//            .collection("attendance").document(date).delete().await()
//        return true
    }

    private suspend fun deleteAtt(model: Attendance): Boolean {
        val monthYear = "${model.date?.year}-${model.date?.month}"
        val reference = serviceLocator.attendanceReference
        reference.child(monthYear).child("${model.date?.day}").child(model.student?.uid.toString())
            .removeValue().await()
        return true
    }

    override suspend fun getAll(): List<Attendance> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllWhere(vararg clause: Any): List<Attendance> {
        val list = serviceLocator.provideStudentsDataSource.getAllWhere(clause[0] as Klass)
        val date = clause[1] as Date
        val monthYear = "${date.year}-${date.month}"
        val day = date.day
        return list.map { getAttendanceStatus(Attendance(it, date = date), date, monthYear, day) }
    }

    private suspend fun getAttendanceStatus(
        model: Attendance,
        date: Date,
        monthYear: String,
        day: Int?
    ): Attendance {
        val student = model.student?.uid.toString()
        val result =
            serviceLocator.attendanceReference.child(monthYear).child("$day").child(student)
                .awaitRealtime()
        Log.e("AAAAAAAAAAAA", "Returning attendance")
        return result.packet?.toAttendance().also {
            it?.student = model.student
            it?.date = date
        } ?: model
    }

    override suspend fun deleteAll(): Boolean {
        TODO("Not yet implemented")
    }

    suspend fun getAllAttendanceYearsForStudentForever(uid: String): List<String> {
        val reference = serviceLocator.attendanceReference
        val result = reference.awaitRealtime()
        val list = mutableListOf<String>()
        result.packet?.children?.map {
            list.add(it.key!!)
        }
        return list
    }
}