// Created by abdif on 8/2/2020

package com.glunode.abuhurerira

import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.*

operator fun <E> MutableLiveData<MutableList<E>>.plusAssign(element: E) {
    val list = this.value ?: mutableListOf()
    list.add(element)
    this.value = list
}


operator fun <E> MutableLiveData<MutableList<E>>.minusAssign(element: E) {
    val list = this.value ?: mutableListOf()
    list.remove(element)
    this.value = list
}

fun <E> MutableList<E>.update(element: E) {
    val list = (this)
    val index = list.indexOf(element)
    set(index, element)
}

fun formatDate(timestamp: Long, format: String?): String? {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat(
        format ?: "MMM dd, yyyy HH:mm",
        Locale.getDefault()
    )
    return sdf.format(date)
}