package com.glunode.abuhurerira.students.info.notes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.glunode.abuhurerira.App
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.formatDate
import com.glunode.api.Note
import com.glunode.api.Student

class AddEditNoteDialog : DialogFragment() {

    private var student: Student? = null
    private var note: Note? = null

    private lateinit var mediator: Mediator

    override fun onCreate(args: Bundle?) {
        super.onCreate(args)
        student = requireArguments().getParcelable("student")
        note = requireArguments().getParcelable("note")
        mediator = requireArguments().getSerializable("mediator") as Mediator
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.info_add_edit_note_dialog, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val textTitle = view.findViewById<TextView>(R.id.title)
        val btnClose =
            view.findViewById<ImageView>(R.id.btn_close)
        btnClose.setOnClickListener { view12: View? -> dialog!!.dismiss() }
        val textNote = view.findViewById<EditText>(R.id.stud_note)
        val textDate = view.findViewById<EditText>(R.id.timestamp)
        val btnSave = view.findViewById<TextView>(R.id.btn_save)
        val text: String?
        val timestamp: String?
        val noteUid: Long
        val newNote: Note
        if (note == null) {
            text = ""
            timestamp = formatDate(System.currentTimeMillis(), null)
            noteUid = System.currentTimeMillis()
            newNote = Note()
            newNote.student = student!!.uid
            textTitle.text = "Add Mistake"
        } else {
            text = note!!.text
            timestamp = note!!.timestamp
            noteUid = note!!.uid!!
            newNote = note!!
            textTitle.text = "Edit Mistake"
        }
        textDate.setText(timestamp)
        textNote.setText(text)
        btnSave.setOnClickListener { view1: View? ->
            if (textNote.text.toString().trim { it <= ' ' }.isEmpty()) {
                textNote.error = "Content is required!"
                return@setOnClickListener
            }
            newNote.uid = noteUid
            newNote.timestamp = textDate.text.toString()
            newNote.text = textNote.text.toString()
            mediator.apply(newNote)
            dialog!!.dismiss()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(student: Student, note: Note?, mediator: Mediator): AddEditNoteDialog {
            val dialog = AddEditNoteDialog()
            val args = Bundle()
            args.putParcelable("student", student)
            args.putParcelable("note", note)
            args.putSerializable("mediator", mediator)
            dialog.arguments = args
            return dialog
        }
    }
}