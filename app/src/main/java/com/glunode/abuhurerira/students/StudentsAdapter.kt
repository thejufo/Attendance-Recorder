package com.glunode.abuhurerira.students

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.databinding.ItemStudentBinding
import com.glunode.api.Student
import java.util.*

class StudentsAdapter(private val klassClickListener: StudentClickListener) :
    RecyclerView.Adapter<StudentsAdapter.ViewHolder>(), Filterable {

    private var studentsFiltered: List<Student?>? = null

    var students = mutableListOf<Student>()
        set(value) {
            field = value
            studentsFiltered = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(
            parent,
            klassClickListener
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        studentsFiltered?.get(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount() = studentsFiltered?.count() ?: 0

    class ViewHolder(
        private val binding: ItemStudentBinding,
        private val clickListener: StudentClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Student) {
            binding.student = model
            binding.listener = clickListener
            binding.executePendingBindings()

            (itemView as CardView).setCardBackgroundColor(
                itemView.context.getColor(
                    when (adapterPosition % 2) {
                        0 -> R.color.colorWhite
                        else -> R.color.colorSilver
                    }
                )
            )
        }

        companion object {

            fun from(parent: ViewGroup, klassClickListener: StudentClickListener): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ItemStudentBinding>(
                    inflater,
                    R.layout.item_student,
                    parent,
                    false
                )
                return ViewHolder(
                    binding,
                    klassClickListener
                )
            }
        }
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                studentsFiltered = if (charString.isEmpty()) students
                else {
                    students.filter {
                        it.name?.toLowerCase(Locale.ROOT)
                            ?.startsWith(charString.toLowerCase(Locale.ROOT))
                            ?: false
                    }
                }
                val results = FilterResults()
                results.values = studentsFiltered
                return results
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                studentsFiltered = filterResults.values as ArrayList<Student>
                notifyDataSetChanged()
            }
        }
    }

    interface StudentClickListener {

        fun onStudentClick(student: Student)
    }
}
