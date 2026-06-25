package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FoodDatabase
import com.example.data.FoodItem
import com.example.data.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FoodRepository

    init {
        val database = FoodDatabase.getDatabase(application, viewModelScope)
        repository = FoodRepository(database.foodDao())
    }

    // Filters and search query state
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("الكل") // "الكل", "فواكه", "خضروات", etc.
    val selectedStatus = MutableStateFlow("الكل")   // "الكل", "طازج", "يوشك على الانتهاء", "منتهي الصلاحية"
    val selectedLocation = MutableStateFlow("الكل") // "الكل", "الثلاجة", "الفريزر", "الرف", "درج الخضروات"
    val sortBy = MutableStateFlow(SortOption.EXPIRY_DATE)
    val alertThresholdDays = MutableStateFlow(3) // Default is 3 days warning

    // Raw food items flow
    val allItems: StateFlow<List<FoodItem>> = repository.allItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered and sorted food items flow
    val filteredItems: StateFlow<List<FoodItem>> = combine(
        combine(allItems, searchQuery, selectedCategory) { items, query, category ->
            Triple(items, query, category)
        },
        combine(selectedStatus, selectedLocation, sortBy) { status, location, sort ->
            Triple(status, location, sort)
        }
    ) { (items, query, category), (status, location, sort) ->
        var result = items

        // 1. Search Query Filter
        if (query.isNotBlank()) {
            result = result.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.notes.contains(query, ignoreCase = true) ||
                it.storageLocation.contains(query, ignoreCase = true) ||
                it.barcode.contains(query, ignoreCase = true)
            }
        }

        // 2. Category Filter
        if (category != "الكل") {
            result = result.filter { it.category == category }
        }

        // 3. Status Filter
        if (status != "الكل") {
            result = result.filter { item ->
                val daysLeft = getDaysRemaining(item.expiryDate)
                when (status) {
                    "طازج" -> daysLeft > 3
                    "يوشك على الانتهاء" -> daysLeft in 0.0..3.0
                    "منتهي الصلاحية" -> daysLeft < 0.0
                    else -> true
                }
            }
        }

        // 4. Location Filter
        if (location != "الكل") {
            result = result.filter { it.storageLocation == location }
        }

        // 5. Sorting
        when (sort) {
            SortOption.NAME -> result.sortedBy { it.name }
            SortOption.EXPIRY_DATE -> result.sortedBy { it.expiryDate }
            SortOption.CREATION_DATE -> result.sortedByDescending { it.creationDate }
            SortOption.QUANTITY -> result.sortedByDescending { it.quantity }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Statistics UI State Flow
    val statsState: StateFlow<StatsSummary> = allItems
        .combine(MutableStateFlow(System.currentTimeMillis())) { items, _ ->
            var fresh = 0
            var warning = 0
            var expired = 0

            items.forEach { item ->
                val daysLeft = getDaysRemaining(item.expiryDate)
                when {
                    daysLeft < 0 -> expired++
                    daysLeft <= 3.0 -> warning++
                    else -> fresh++
                }
            }

            StatsSummary(
                totalItems = items.size,
                freshCount = fresh,
                warningCount = warning,
                expiredCount = expired
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatsSummary(0, 0, 0, 0)
        )

    // Database Actions
    fun insertItem(item: FoodItem) {
        viewModelScope.launch {
            repository.insert(item)
        }
    }

    fun updateItem(item: FoodItem) {
        viewModelScope.launch {
            repository.update(item)
        }
    }

    fun deleteItem(item: FoodItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun deleteItemById(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    // Helper functions
    private fun getDaysRemaining(expiryTimestamp: Long): Double {
        val diffMs = expiryTimestamp - System.currentTimeMillis()
        return diffMs / (24.0 * 60.0 * 60.0 * 1000.0)
    }
}

enum class SortOption(val displayNameArabic: String) {
    NAME("الاسم"),
    EXPIRY_DATE("تاريخ الانتهاء"),
    CREATION_DATE("تاريخ الإضافة"),
    QUANTITY("الكمية")
}

data class StatsSummary(
    val totalItems: Int,
    val freshCount: Int,
    val warningCount: Int,
    val expiredCount: Int
)
