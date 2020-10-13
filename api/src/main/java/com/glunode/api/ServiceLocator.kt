// Created by abdif on 8/8/2020

package com.glunode.api

import com.glunode.api.data.impl.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase

internal object ServiceLocator {

    private fun firestore() = Firebase.firestore.apply {
        firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
        }
    }

    private lateinit var database: FirebaseDatabase

    init {
        if (!::database.isInitialized) {
            database = Firebase.database
            database.setPersistenceEnabled(true)
        }
    }

    val provideKlassesDataSource = KlassesDataSource(this)
    val provideStudentsDataSource = StudentsDataSource(this)
    val provideAttendanceDataSource = AttendanceDataSource(this)
    val provideNotesDataSource = NotesDataSource(this)
    val provideReportDataSource = ReportDataSource(this)

    val klassesReference = database.getReference("klasses")
    val studentsReference = database.getReference("students")
    val attendanceReference = database.getReference("attendance")
    val notesReference = database.getReference("notes")
}