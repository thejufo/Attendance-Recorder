package com.glunode.abuhurerira.students.info.notes

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.glunode.abuhurerira.App
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.databinding.InfoNotesFargmentBinding
import com.glunode.api.Note
import com.glunode.api.Student
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NotesFragment : Fragment(), NotesAdapter.NotePopupListener, Mediator {

    private val viewModel by viewModels<NotesViewModel> {
        this.viewLifecycleOwner;
        NotesViewModel.Factory((requireContext().applicationContext as App).appRepo)
    }

    private lateinit var binding: InfoNotesFargmentBinding

    private val student by lazy { arguments?.getParcelable("student") as Student? }

    private lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<InfoNotesFargmentBinding>(
            inflater,
            R.layout.info_notes_fargment,
            container,
            false
        )
            .also {
                binding = it
                binding.viewModel = viewModel
                binding.lifecycleOwner = this
            }.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        notesAdapter = NotesAdapter(this)

        binding.notesContainer.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = notesAdapter
        }
    }

    private fun showMistakesDialog() {
        val dialog: DialogFragment =
            AddEditNoteDialog.newInstance(student!!, null, this)
        dialog.isCancelable = false
        dialog.show(childFragmentManager, AddEditNoteDialog::class.java.simpleName)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadNotes(student?.uid.toString())
        viewModel.notes.observe(viewLifecycleOwner, Observer {
            notesAdapter.data = it.toMutableList()
        })
    }

    fun onAddNote() {
        showMistakesDialog()
    }

    override fun onModify(note: Note) {
        val dialog = AddEditNoteDialog.newInstance(student!!, note, this)
        dialog.isCancelable = false
        dialog.show(childFragmentManager, AddEditNoteDialog::class.java.simpleName)
    }

    override fun onDelete(note: Note) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Delete note?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteNote(note)
            }
            .setNegativeButton(
                "NO"
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.show()
    }

    override fun apply(note: Note) {
        viewModel.addNote(note)
    }

    companion object {

        fun newInstance(student: Student): NotesFragment {
            val frag = NotesFragment()
            frag.arguments = bundleOf("student" to student)
            return frag
        }
    }
}