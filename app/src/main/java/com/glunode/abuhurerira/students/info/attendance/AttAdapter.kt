package com.glunode.abuhurerira.students.info.attendance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.databinding.DataItemBinding
import com.glunode.api.Attendance

class AttAdapter :
    RecyclerView.Adapter<AttAdapter.ViewHolder>() {

    var data = listOf<Attendance>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder.fom(parent)

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val attendance = data[position]
        holder.bind(attendance)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(val binding: DataItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(attendance: Attendance) {
            binding.attendance = attendance
            binding.executePendingBindings()
        }

        companion object {

            fun fom(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.data_item,
                        parent,
                        false
                    )
                )
            }
        }
    }
}