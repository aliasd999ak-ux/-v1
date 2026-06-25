package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // "فواكه", "خضروات", "ألبان", "لحوم", "مخبوزات", "أخرى"
    val creationDate: Long = System.currentTimeMillis(),
    val expiryDate: Long,
    val storageLocation: String, // "الثلاجة", "الفريزر", "الرف", "درج الخضروات"
    val imageUri: String? = null, // Path to camera-captured image
    val presetIcon: String? = null, // Preset key for beautiful UI fallback
    val quantity: Double = 1.0,
    val unit: String = "قطعة", // "كجم", "قطعة", "لتر", "علبة", "كيس"
    val notes: String = "",
    val barcode: String = ""
) : Serializable
