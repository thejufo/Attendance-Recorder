// Created by abdif on 8/7/2020

package com.glunode.api

import android.os.Parcelable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Klass(
    var uid: Long = -1,
    var name: String? = null
) : Parcelable

@Parcelize
data class Student(
    var no: Int = -1,
    var uid: Long = -1,
    var avatar: String? = null,
    var name: String? = null,
    var klass: Klass? = null,
    var phoneNumber1: String? = null,
    var phoneNumber2: String? = null,
    var phoneNumber3: String? = null,
    var mark: String? = null,
    var archived: Boolean = false
) : Parcelable

@Parcelize
data class Attendance(
    var student: Student? = null,
    var status: String? = null,
    var date: Date? = null
) : Parcelable

@Parcelize
data class Note(
    var student: Long? = null,
    var uid: Long? = null,
    var text: String? = null,
    var timestamp: String? = null
) : Parcelable

@Parcelize
data class Date(
    var year: Int? = null,
    var month: String? = null,
    var day: Int? = null,
    var dayStr: String? = null
) : Parcelable

@Parcelize
data class Report(
    var student: Student?,
    var report: String? = null
) : Parcelable

data class QueryResponse(val packet: DataSnapshot?, val error: DatabaseError?)


