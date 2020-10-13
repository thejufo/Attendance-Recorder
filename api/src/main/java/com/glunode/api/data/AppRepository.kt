// Created by abdif on 8/6/2020

package com.glunode.api.data

import android.net.Uri
import com.glunode.api.*
import com.glunode.api.data.impl.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AppRepository internal constructor(
    private val klassesDataSource: KlassesDataSource,
    private val studentsDataSource: StudentsDataSource,
    private val attendanceDataSource: AttendanceDataSource,
    private val notesDataSource: NotesDataSource,
    private val reportDataSource: ReportDataSource
) {

    suspend fun addKlass(klass: Klass) = klassesDataSource.add(klass)

    suspend fun updateKlass(klass: Klass) = klassesDataSource.add(klass)

    suspend fun deleteKlass(klass: Klass) = klassesDataSource.delete(klass)

    suspend fun getAllKlasses() = klassesDataSource.getAll()

    suspend fun deleteAllKlasses() = klassesDataSource.deleteAll()

    suspend fun addStudent(student: Student) = studentsDataSource.add(student)

    suspend fun updateStudent(student: Student) = studentsDataSource.update(student)

    suspend fun deleteStudent(student: Student) = studentsDataSource.delete(student)

    suspend fun getAllStudents() = studentsDataSource.getAll()

    suspend fun getAllStudentsWhere(klass: Klass) = studentsDataSource.getAllWhere(klass as Any)

    suspend fun getArchivedStudents() = studentsDataSource.getArchived()

    suspend fun deleteAllStudents() = studentsDataSource.deleteAll()

    suspend fun addAttendance(attendance: Attendance) = attendanceDataSource.add(attendance)

    suspend fun deleteAttendance(attendance: Attendance) = attendanceDataSource.delete(attendance)

    suspend fun getAttendanceList(klass: Klass, date: Date) =
        attendanceDataSource.getAllWhere(klass, date)

    suspend fun getAllAttendanceYearsForStudentForever(uid: String) =
        attendanceDataSource.getAllAttendanceYearsForStudentForever(uid)

    suspend fun getAllAttendancesForStudentForever(uid: String) =
        attendanceDataSource.getAllAttendancesForStudentForever(uid)

    suspend fun getAllAttendancesForStudentPerYearMonth(uid: String, monthYear: String) =
        attendanceDataSource.getAllAttendancesForStudentPerYearMonth(uid, monthYear)

    suspend fun addNote(note: Note) = notesDataSource.add(note)

    suspend fun getAllNotes(uid: String) = notesDataSource.getAllWhere(uid)

    suspend fun deleteNote(note: Note) = notesDataSource.delete(note)

    suspend fun getReports(klass: Klass, date: Date) = reportDataSource.generateReport(klass, date)

    fun uploadImageToFirebaseStorage(
        selectedPhotoUri: Uri,
        student: String,
        callback: (downloadUrl: Uri) -> Unit
    ) =
        studentsDataSource.uploadImageToFirebaseStorage(selectedPhotoUri, student, callback)

    suspend fun deleteAvatarFromFirebase(student: Student) =
        studentsDataSource.deleteAvatarFromFirebase(student)

    fun checkCurrentUser(callback: (isLoggedIn: Boolean) -> Unit) {
        callback(Firebase.auth.currentUser != null)
    }

    fun login(callback: (success: Boolean, error: Exception?) -> Unit) {
        Firebase.auth.signInAnonymously().addOnCompleteListener {
            callback(it.isSuccessful, it.exception)
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: AppRepository? = null

        fun getDefault() = INSTANCE ?: synchronized(this) {
            val sl = ServiceLocator
            return@synchronized AppRepository(
                sl.provideKlassesDataSource,
                sl.provideStudentsDataSource,
                sl.provideAttendanceDataSource,
                sl.provideNotesDataSource,
                sl.provideReportDataSource
            )
        }
    }
}