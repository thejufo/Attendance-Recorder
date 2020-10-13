// Created by abdif on 8/20/2020

package com.glunode.api.data.impl

import android.util.Log
import com.glunode.api.*

internal class ReportDataSource(private val serviceLocator: ServiceLocator) {

    suspend fun generateReport(klass: Klass, date: Date): List<Report> {
        val monthYear = "${date.year}-${date.month}"
        val returnVal = mutableListOf<Report>()
        val students = serviceLocator.provideStudentsDataSource.getAllWhere(klass)

        for (student in students) {
            var absents = 0
            var noUniforms = 0
            var lates = 0
            serviceLocator.provideAttendanceDataSource.getAllAttendancesForStudentPerYearMonth(
                student.uid.toString(),
                monthYear
            ).forEach {
                Log.e("AAAAAAAA", it.status!!)
                when (it.status) {
                    "Absent",
                    "Abs after br" -> absents++
                    "No uniform" -> noUniforms++
                    "Late before br",
                    "Late after br",
                    "(Late)" -> lates++
                }
            }

            if (absents < 3 || lates < 5)
                continue

            val reportString = "$absents Absents - $noUniforms No Uniforms - $lates Lates"
            returnVal.add(Report(student, reportString))
        }
        return returnVal
    }
}