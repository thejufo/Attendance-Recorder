package com.glunode.abuhurerira.attendance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.databinding.ItemAttendanceTableBinding
import com.glunode.api.Attendance
import com.glunode.api.Student
import timber.log.Timber

class AttendanceAdapter(
    private val listener: AttendanceTableListener
) : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    var data = mutableListOf<Attendance>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent, listener)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.count()

    class ViewHolder(
        private val binding: ItemAttendanceTableBinding,
        private val listener: AttendanceTableListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Attendance) {
            with(binding) {
                attendance = model
                listener = this@ViewHolder.listener
                executePendingBindings()
            }

            (itemView).setBackgroundColor(
                itemView.context.getColor(
                    when (adapterPosition % 2) {
                        0 -> R.color.colorWhite
                        else -> R.color.colorSilver
                    }
                )
            )
        }

        companion object {

            fun from(parent: ViewGroup, listener: AttendanceTableListener): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ItemAttendanceTableBinding>(
                    inflater,
                    R.layout.item_attendance_table,
                    parent,
                    false
                )
                return ViewHolder(binding, listener)
            }
        }
    }

    interface AttendanceTableListener {

        fun onStudentNameClick(view: View?, attendance: Attendance)

        fun onStatusClick(view: View?, attendance: Attendance)
    }
}