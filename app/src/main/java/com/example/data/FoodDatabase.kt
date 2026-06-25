package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [FoodItem::class], version = 2, exportSchema = false)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "food_inventory_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(FoodDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class FoodDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.foodDao())
                }
            }
        }

        suspend fun populateDatabase(foodDao: FoodDao) {
            // Delete all content (optional, but it's empty anyway on create)
            val now = System.currentTimeMillis()
            val oneDayMs = 24 * 60 * 60 * 1000L

            // Pre-populate with beautiful, localized food items across all states
            val item1 = FoodItem(
                name = "تفاح أحمر طازج",
                category = "فواكه",
                creationDate = now,
                expiryDate = now + (8 * oneDayMs), // Fresh (8 days left)
                storageLocation = "الثلاجة",
                quantity = 6.0,
                unit = "قطعة",
                notes = "تفاح أحمر سكري وغني بالفيتامينات. يفضل حفظه بارداً.",
                barcode = "62810001"
            )

            val item2 = FoodItem(
                name = "طماطم بلدية",
                category = "خضروات",
                creationDate = now - (2 * oneDayMs),
                expiryDate = now + (2 * oneDayMs), // Expiring soon (2 days left)
                storageLocation = "درج الخضروات",
                quantity = 1.5,
                unit = "كجم",
                notes = "مشتراة من سوق المزارعين. ممتازة للسلطة والطهي.",
                barcode = "62810002"
            )

            val item3 = FoodItem(
                name = "حليب طازج كامل الدسم",
                category = "ألبان",
                creationDate = now - (3 * oneDayMs),
                expiryDate = now + (1 * oneDayMs), // Expiring very soon (1 day left)
                storageLocation = "الثلاجة",
                quantity = 1.0,
                unit = "لتر",
                notes = "عبوة حليب مبستر. ممتازة مع الشاي والقهوة الصباحية.",
                barcode = "62810003"
            )

            val item4 = FoodItem(
                name = "خبز توست نخالة",
                category = "مخبوزات",
                creationDate = now - (6 * oneDayMs),
                expiryDate = now - (1 * oneDayMs), // Already Expired (1 day ago)
                storageLocation = "الرف",
                quantity = 1.0,
                unit = "كيس",
                notes = "خبز بالحبوب الكاملة. تذكر التخلص منه أو تحويله إلى بقسماط.",
                barcode = "62810004"
            )

            val item5 = FoodItem(
                name = "صدر دجاج مبرد",
                category = "لحوم",
                creationDate = now,
                expiryDate = now + (3 * oneDayMs), // Expiring soon (3 days left)
                storageLocation = "الفريزر",
                quantity = 500.0,
                unit = "جرام",
                notes = "دجاج طازج نظيف وخالي من العظم والجلد.",
                barcode = "62810005"
            )

            foodDao.insertItem(item1)
            foodDao.insertItem(item2)
            foodDao.insertItem(item3)
            foodDao.insertItem(item4)
            foodDao.insertItem(item5)
        }
    }
}
