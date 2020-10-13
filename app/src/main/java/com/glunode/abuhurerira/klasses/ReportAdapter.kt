package com.glunode.abuhurerira.klasses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.glunode.abuhurerira.QuickHelper
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.databinding.ReportItemBinding
import com.glunode.api.Report

class ReportAdapter :
    RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    var reports = mutableListOf<Report>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reports[position])
    }

    override fun getItemCount() = reports.size

    class ViewHolder(
        private val binding: ReportItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Report) {
            binding.report = model
            binding.executePendingBindings()

            itemView.setOnClickListener {
                model.student?.run {
                    QuickHelper.goToStudentDetails(it.context, this)
                }
            }
        }

        companion object {

            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ReportItemBinding>(
                    inflater,
                    R.layout.report_item,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }
}
