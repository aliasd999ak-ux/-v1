package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM food_items ORDER BY expiryDate ASC")
    fun getAllItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE id = :id")
    fun getItemById(id: Long): Flow<FoodItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: FoodItem): Long

    @Update
    suspend fun updateItem(item: FoodItem)

    @Delete
    suspend fun deleteItem(item: FoodItem)

    @Query("DELETE FROM food_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)
}
