// Created by abdif on 8/7/2020

package com.glunode.api.data

interface DataSource<T> {

    suspend fun add(model: T): Boolean

    suspend fun get(clause: Any): T?

    suspend fun update(model: T): Boolean

    suspend fun delete(model: T): Boolean

    suspend fun getAll(): List<T>

    suspend fun getAllWhere(vararg clause: Any): List<T> {
        TODO("Not yet implemented")
    }

    suspend fun deleteAll(): Boolean
}
