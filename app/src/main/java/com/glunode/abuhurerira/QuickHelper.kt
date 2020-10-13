// Created by abdif on 8/2/2020

package com.glunode.abuhurerira

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException
import com.glunode.abuhurerira.students.info.InfoActivity
import com.glunode.abuhurerira.students.info.profile.PhotoViewActivity
import com.glunode.api.Klass
import com.glunode.api.Student
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class QuickHelper {

    companion object {

        fun showConfirmDialog(
            context: Context,
            title: String? = null,
            msg: String? = null,
            block: () -> Unit
        ) {
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Sure") { _, _ ->
                    block()
                }
                .setNegativeButton("Nope") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        fun clearInputs(vararg inputs: EditText) {
            inputs.forEach { it.text.clear() }
        }

        fun showCalendarDialog(context: Context, function: (eventDay: EventDay) -> Unit) {
            val alertDialog = MaterialAlertDialogBuilder(context).create()
            val calendarView =
                LayoutInflater.from(context)
                    .inflate(R.layout.date_picker, null, false) as CalendarView?
            try {
                calendarView?.setDate(Calendar.getInstance().time)
            } catch (e: OutOfDateRangeException) {
            }

            calendarView?.setOnDayClickListener { eventDay: EventDay ->
                alertDialog.dismiss()
                function(eventDay)
            }

            alertDialog.setView(calendarView)
            alertDialog.show()
        }

        fun goToStudentDetails(context: Context, student: Student) {
            context.startActivity(Intent(context, InfoActivity::class.java)
                .apply {
                    putExtra("student", student)
                })
        }

        fun goToStudentPhotoView(context: Context, student: Student) {
            context.startActivity(Intent(context, PhotoViewActivity::class.java)
                .apply {
                    putExtra("student", student)
                })
        }

        fun showAddEditKlassDialog(
            context: Context,
            oldName: String?,
            function: (name: String) -> Unit
        ) {
            val nameInput = EditText(context).apply {
                hint = "Name"
                setText(oldName)
            }
            MaterialAlertDialogBuilder(context)
                .setView(nameInput)
                .setPositiveButton("Save") { _, _ ->
                    function(nameInput.text.toString())
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        fun addEditStudentDialog(context: Context, klass: Klass, oldStudent: Student?, callback: (student: Student) -> Unit) {
            val root = LayoutInflater.from(context).inflate(R.layout.dialog_add_student, null, false)
            val etNo = root.findViewById<EditText>(R.id.stud_number)
            val etName = root.findViewById<EditText>(R.id.stud_name)
            val etPhoneNo1 = root.findViewById<EditText>(R.id.stud_parent_phone1)
            val etPhoneNo2 = root.findViewById<EditText>(R.id.stud_parent_phone2)
            val etPhoneNo3 = root.findViewById<EditText>(R.id.stud_phone_number)

            val newStudent = oldStudent ?: Student()

            oldStudent?.let {
                etNo.setText(oldStudent.no.toString())
                etName.setText(oldStudent.name.toString())
                etPhoneNo3.setText(oldStudent.phoneNumber3.toString())
                etPhoneNo1.setText(oldStudent.phoneNumber1.toString())
                etPhoneNo2.setText(oldStudent.phoneNumber2.toString())
            }

            val alert = MaterialAlertDialogBuilder(context)
                .setView(root)
                .setPositiveButton("Save") { _, _ -> }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNeutralButton("Clear") { _, _ -> }
                .create()
            alert.show()
            alert.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                clearInputs(etNo, etName, etPhoneNo1, etPhoneNo3)
                etNo.requestFocus()
            }

            alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (etNo.text.toString().isEmpty() || etName.text.toString().isEmpty()) {
                    Toast.makeText(context, "Name and No are required!", Toast.LENGTH_SHORT).show()
                } else {
                    val no = etNo.text.toString().toInt()
                    val name = etName.text.toString()
                    if (etPhoneNo1.text.toString().isNotEmpty()) {
                        val number = etPhoneNo1.text.toString()
                        newStudent.phoneNumber1 = number
                    }
                    if (etPhoneNo2.text.toString().isNotEmpty()) {
                        val number = etPhoneNo2.text.toString()
                        newStudent.phoneNumber2 = number
                    }
                    if (etPhoneNo3.text.toString().isNotEmpty()) {
                        val number = etPhoneNo3.text.toString()
                        newStudent.phoneNumber3 = number
                    }
                    newStudent.no = no
                    newStudent.name = name
                    newStudent.klass = klass
                    newStudent.archived = false

                    callback(newStudent)

                    clearInputs(etNo, etName, etPhoneNo1, etPhoneNo2, etPhoneNo3)
                    etNo.requestFocus()
                }
            }
        }

        fun showCallMessagePopup(view: View, student: Student?, callback: (number: String) -> Boolean) {
            val popup = PopupMenu(view.context, view)
            popup.menu.add(student?.phoneNumber1.toString())
            popup.menu.add(student?.phoneNumber2.toString())
            popup.menu.add(student?.phoneNumber3.toString())
            popup.setOnMenuItemClickListener {
                callback(it.title.toString())
            }
            popup.show()
        }

        fun makePhoneCall(context: Context, phone: String) {
            if (context.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "You should allow the app to make phone calls", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phone")
                context.startActivity(intent)
            }
        }
    }
}