package com.glunode.abuhurerira.students.info.attendance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.glunode.abuhurerira.R
import java.util.*

class YearsListAdapter(private val listener: OnSelectionListener) :
    RecyclerView.Adapter<YearsListAdapter.ViewHolder>() {

    var years = setOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.info_item_year, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val year = ArrayList(years)[position]
        holder.year.text = year
        holder.itemView.setOnClickListener { v: View? ->
            listener.onYearSelected(year)
        }
    }

    override fun getItemCount(): Int {
        return years.size
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val year: TextView = itemView.findViewById(R.id.year_text)

    }

    interface OnSelectionListener {

        fun onYearSelected(year: String)
    }
}