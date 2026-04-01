package com.example.zorvyn_task.data.repository

import com.example.zorvyn_task.data.local.TransactionDao
import com.example.zorvyn_task.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {

    fun getAllTransactions(): Flow<List<TransactionEntity>> = dao.getAllTransactions()

    suspend fun insert(transaction: TransactionEntity) = dao.insert(transaction)

    suspend fun update(transaction: TransactionEntity) = dao.update(transaction)

    suspend fun delete(transaction: TransactionEntity) = dao.delete(transaction)
}