// Created by abdif on 8/21/2020

package com.glunode.abuhurerira.students.info.notes

import com.glunode.api.Note
import java.io.Serializable


interface Mediator : Serializable {

    companion object {
        const val serialVersionUID = 0L
    }

    abstract fun apply(note: Note)
}