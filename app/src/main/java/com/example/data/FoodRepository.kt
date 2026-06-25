package com.example.data

import kotlinx.coroutines.flow.Flow

class FoodRepository(private val foodDao: FoodDao) {
    val allItems: Flow<List<FoodItem>> = foodDao.getAllItems()

    fun getItemById(id: Long): Flow<FoodItem?> = foodDao.getItemById(id)

    suspend fun insert(item: FoodItem): Long = foodDao.insertItem(item)

    suspend fun update(item: FoodItem) = foodDao.updateItem(item)

    suspend fun delete(item: FoodItem) = foodDao.deleteItem(item)

    suspend fun deleteById(id: Long) = foodDao.deleteItemById(id)
}
