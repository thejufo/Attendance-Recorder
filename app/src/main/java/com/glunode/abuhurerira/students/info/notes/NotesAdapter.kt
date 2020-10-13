package com.glunode.abuhurerira.students.info.notes

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.databinding.NoteItemBinding
import com.glunode.api.Note
import com.glunode.api.Student
import timber.log.Timber

class NotesAdapter(private val notePopupListener: NotePopupListener) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    private val student: Student? = null

    var data = mutableListOf<Note>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder.from(parent, notePopupListener)

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val note = data[position]
        holder.bind(note)
    }

    override fun getItemCount() = data.size

    class ViewHolder private constructor(
        private val binding: NoteItemBinding,
        private val notePopupListener: NotePopupListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            Timber.e(note.toString())
            binding.note = note
            binding.executePendingBindings()

            itemView.setOnClickListener { view: View ->
                val menu = PopupMenu(view.context, view)
                menu.menu.add("Modify")
                menu.menu.add("Delete")
                menu.setOnMenuItemClickListener { menuItem: MenuItem ->
                    if (menuItem.title.toString() == "Modify") {
                        notePopupListener.onModify(note)
                        return@setOnMenuItemClickListener true
                    }
                    if (menuItem.title.toString() == "Delete") {
                        notePopupListener.onDelete(note)
                        return@setOnMenuItemClickListener true
                    }
                    false
                }
                menu.show()
            }
        }

        companion object {

            fun from(parent: ViewGroup, notePopupListener: NotePopupListener) = ViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.note_item,
                    parent,
                    false
                ),
                notePopupListener
            )
        }
    }

    interface NotePopupListener {

        fun onModify(note: Note)

        fun onDelete(note: Note)
    }
}