// Created by abdif on 8/4/2020

package com.glunode.abuhurerira

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.glunode.api.Attendance
import com.glunode.api.Student

@BindingAdapter("setVisible")
fun View.setVisible(bool: Boolean) {
    isVisible = bool
}

@BindingAdapter("loadAvatar")
fun loadAvatar(view: ImageView?, student: Student?) {
    if (student != null) {
        if (view != null) {
            Glide.with(view).load(student.avatar ?: "").placeholder(R.drawable.no_image2)
                .fallback(R.drawable.no_image2).into(view)
        }
    }
}

@BindingAdapter("filterAttColor")
fun TextView.filterAttColor(status: String?) {
    status?.let {
        val statusColor = when (it) {
            "Excused abs" -> context.getColor(R.color.primaryColor)
            "Late before br", "Late after br" -> context.getColor(R.color.colorYellow)
            "No uniform" -> context.getColor(R.color.colorGreen)
            else -> context.getColor(R.color.colorRed)
        }
        setTextColor(statusColor)
    }
}

@BindingAdapter("formatDateForReport")
fun TextView.formatDateForReport(attendance: Attendance) {
    text = "${attendance.date?.year}-${attendance.date?.month}-${attendance.date?.day}, ${attendance.date?.dayStr}"
}

@BindingAdapter("setMarkVisible")
fun TextView.setMarkVisible(mark: String?) {
    if (!mark.isNullOrEmpty() && mark.trim().isNotEmpty()) {
        when (mark) {
            "BOLD" -> {
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(context.getColor(android.R.color.primary_text_light))
            }
            "RED" -> {
                typeface = Typeface.DEFAULT
                setTextColor(Color.RED)
            }
            "BLUE" -> {
                typeface = Typeface.DEFAULT
                setTextColor(Color.BLUE)
            }
        }
    } else {
        typeface = Typeface.DEFAULT
        setTextColor(context.getColor(android.R.color.primary_text_light))
    }
}
