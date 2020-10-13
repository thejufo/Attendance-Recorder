// Created by abdif on 8/11/2020

package com.glunode.api

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun DatabaseReference.awaitRealtime() = suspendCancellableCoroutine<QueryResponse> {continuation->
    addListenerForSingleValueEvent(object : ValueEventListener{
        override fun onCancelled(error: DatabaseError) {
            continuation.resume(QueryResponse(null, error))
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            continuation.resume(QueryResponse(snapshot, null))
        }
    })
}

internal fun Student.toHashMap() = hashMapOf(
    "uid" to uid,
    "no" to no,
    "name" to name,
    "avatar" to avatar,
    "klass" to klass?.uid,
    "phoneNumber1" to phoneNumber1,
    "phoneNumber2" to phoneNumber2,
    "phoneNumber3" to phoneNumber3,
    "mark" to mark,
    "archived" to archived
)

internal suspend fun DataSnapshot.toStudent() =
    Student().also {
        it.uid = this.child("uid").value as Long
        it.no = (this.child("no").value as Long).toInt()
        it.name = this.child("name").value as String
        it.avatar = this.child("avatar").value as String?
//        it.klass = getStudentKlass(this.child("klass").value as Long)
        it.phoneNumber1 = this.child("phoneNumber1").value.toString()
        it.phoneNumber2 = this.child("phoneNumber2").value.toString()
        it.phoneNumber3 = this.child("phoneNumber3").value.toString()
        it.mark = this.child("mark").value as String?
        it.archived = this.child("archived").value as Boolean
    }


internal suspend fun DataSnapshot.toStudent1() =
    Student().also {
        it.uid = this.child("uid").value as Long
        it.no = (this.child("no").value as Long).toInt()
        it.name = this.child("name").value as String
        it.avatar = this.child("avatar").value as String?
        if (it.klass == null) {
            Log.e("AAAaa", it.klass.toString())
        }
        try {
            it.klass = getStudentKlass(this.child("klass").value as Long)
        } catch (e: TypeCastException) {
            e.printStackTrace()
        }
        it.phoneNumber1 = this.child("phoneNumber1").value.toString()
        it.phoneNumber2 = this.child("phoneNumber2").value.toString()
        it.phoneNumber3 = this.child("phoneNumber3").value.toString()
        it.mark = this.child("mark").value as String?
        it.archived = this.child("archived").value as Boolean
    }

internal suspend fun getStudentKlass(klass: Long): Klass? {
    return ServiceLocator.provideKlassesDataSource.get(klass)
}

internal fun Attendance.toHashMap() = hashMapOf(
    "student" to student?.uid,
    "date" to date,
    "status" to status
)

internal fun DataSnapshot.toAttendance() = try {
    if (this.exists()) {
        Attendance().also {
            it.status = this.child("status").value as String?
        }
    } else null
} catch (e: Exception) {
    e.printStackTrace()
    null
}

internal fun DataSnapshot.toAttendance1() = try {
    if (this.exists()) {
        Attendance().also {
            it.date = toDate(this.child("date").value as Map<String, Any>)
            it.status = this.child("status").value as String?
        }
    } else null
} catch (e: Exception) {
    e.printStackTrace()
    null
}

internal fun toDate(map: Map<String, Any>) = Date().also {
    it.day = (map["day"] as Long).toInt()
    it.dayStr = map["dayStr"] as String
    it.year = (map["year"]as Long).toInt()
    it.month = map["month"]as String
}
