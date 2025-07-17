//////package com.example.parttracker.data
//////
//////import android.content.Context
//////import androidx.room.Database
//////import androidx.room.Room
//////import androidx.room.RoomDatabase
//////import com.example.parttracker.model.ScannedPart
//////import com.example.parttracker.model.UsedPartCount
//////
//////@Database(
//////    entities = [ScannedPart::class, UsedPart::class],
//////    version = 3,
//////    exportSchema = false
//////)
//////abstract class PartDatabase : RoomDatabase() {
//////    abstract fun scannedPartDao(): ScannedPartDao
//////    abstract fun usedPartDao(): UsedPartDao
//////    abstract fun dashboardEntryDao(): DashboardEntryDao
//////
//////
//////    companion object {
//////        @Volatile
//////        private var INSTANCE: PartDatabase? = null
//////
//////        fun getRoomDatabase(context: Context): PartDatabase {
//////            return INSTANCE ?: synchronized(this) {
//////                val instance = Room.databaseBuilder(
//////                    context.applicationContext,
//////                    PartDatabase::class.java,
//////                    "part_database"
//////                ).build()
//////                INSTANCE = instance
//////                instance
//////            }
//////        }
//////    }
//////}
////
////
////package com.example.parttracker.data
////
////import android.content.Context
////import androidx.room.Database
////import androidx.room.Room
////import androidx.room.RoomDatabase
////import com.example.parttracker.model.ScannedPart
////import com.example.parttracker.data.UsedPart
////import com.example.parttracker.model.DashboardEntry
////import com.example.parttracker.model.ManualPartEntry
////
////@Database(
////    entities = [ScannedPart::class, UsedPart::class, DashboardEntry::class, ManualPartEntry::class],
////    version = 4, // Increase version if you changed schema
////    exportSchema = false
////)
////abstract class PartDatabase : RoomDatabase() {
////
////    abstract fun scannedPartDao(): ScannedPartDao
////    abstract fun usedPartDao(): UsedPartDao
////    abstract fun dashboardEntryDao(): DashboardEntryDao
////
////    companion object {
////        @Volatile
////        private var INSTANCE: PartDatabase? = null
////
////        fun getRoomDatabase(context: Context): PartDatabase {
////            return INSTANCE ?: synchronized(this) {
////                val instance = Room.databaseBuilder(
////                    context.applicationContext,
////                    PartDatabase::class.java,
////                    "part_database"
////                )
////                    .fal lbackToDestructiveMigration() // Use this if you changed schema & are not using migration
////                    .build()
////                INSTANCE = instance
////                instance
////            }
////        }
////    }
////}
//
//
//package com.example.parttracker.data
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import com.example.parttracker.model.ScannedPart
//import com.example.parttracker.data.UsedPart
//import com.example.parttracker.model.DashboardEntry
//import com.example.parttracker.model.PlanEntry
//import com.example.parttracker.model.DashboardRow
//import com.example.parttracker.model.ModelProduction
//
//
//@Database(
//    entities = [ScannedPart::class, UsedPart::class, DashboardEntry::class, PlanEntry::class, DashboardRow::class, ModelProduction::class],
//    version = 25,
//    exportSchema = false
//)
//abstract class PartDatabase : RoomDatabase() {
//
//    abstract fun scannedPartDao(): ScannedPartDao
//    abstract fun usedPartDao(): UsedPartDao
//    abstract fun dashboardEntryDao(): DashboardEntryDao
//    abstract fun planDao(): PlanDao
//    abstract fun modelProductionDao(): ModelProductionDao
//
//
//    // ✅ Add this missing DAO
//    //abstract fun manualPartEntryDao(): ManualPartEntryDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: PartDatabase? = null
//
//        fun getRoomDatabase(context: Context): PartDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    PartDatabase::class.java,
//                    "part_database"
//                )
//                    .fallbackToDestructiveMigration()
//                    .build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}
//


//package com.example.parttracker.data
//
//import android.content.Context
//import androidx.room.*
//import androidx.sqlite.db.SupportSQLiteDatabase
//import com.example.parttracker.model.*
//
//@Database(
//    entities = [
//        ScannedPart::class,
//        UsedPart::class,
//        DashboardEntry::class,
//        PlanEntry::class,
//        DashboardRow::class,
//        ModelProduction::class
//    ],
//    version = 28,
//    exportSchema = true
//)
//abstract class PartDatabase : RoomDatabase() {
//
//    abstract fun scannedPartDao(): ScannedPartDao
//    abstract fun usedPartDao(): UsedPartDao
//    abstract fun dashboardEntryDao(): DashboardEntryDao
//    abstract fun planDao(): PlanDao
//    abstract fun modelProductionDao(): ModelProductionDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: PartDatabase? = null
//
//        fun getRoomDatabase(context: Context): PartDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    PartDatabase::class.java,
//                    "part_database"
//                )
//                    // No fallbackToDestructiveMigration() — so data won't be wiped
//                    .build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}


package com.example.parttracker.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.parttracker.model.*

//@Database(
//    entities = [
//        ScannedPart::class,
//        UsedPart::class,
//        DashboardEntry::class,
//        PlanEntry::class,
//        DashboardRow::class,
//        ModelProduction::class
//    ],
//    version = 28,
//    exportSchema = true
//)
//abstract class PartDatabase : RoomDatabase() {
//
//    abstract fun scannedPartDao(): ScannedPartDao
//    abstract fun usedPartDao(): UsedPartDao
//    abstract fun dashboardEntryDao(): DashboardEntryDao
//    abstract fun planDao(): PlanDao
//    abstract fun modelProductionDao(): ModelProductionDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: PartDatabase? = null
//
//        // ✅ Define Migration from 27 to 28
//        private val MIGRATION_27_28 = object : Migration(27, 28) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // Replace with your real migration
//                // Example: Adding a new column to scanned_part_table
//                database.execSQL("ALTER TABLE scanned_part_table ADD COLUMN trolleyName TEXT")
//                database.execSQL("ALTER TABLE scanned_part_table ADD COLUMN trolleyNumber TEXT")
//            }
//        }
//
//        fun getRoomDatabase(context: Context): PartDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    PartDatabase::class.java,
//                    "part_database"
//                )
//                    .addMigrations(MIGRATION_27_28) // ✅ Register the migration
//                    .build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}

@Database(
    entities = [
        ScannedPart::class,
        UsedPart::class,
        DashboardEntry::class,
        PlanEntry::class,
        DashboardRow::class,
        ModelProduction::class
    ],
    version = 36, // ⬆️ Increment version
    exportSchema = true
)
abstract class PartDatabase : RoomDatabase() {
    abstract fun scannedPartDao(): ScannedPartDao
    abstract fun usedPartDao(): UsedPartDao
    abstract fun dashboardEntryDao(): DashboardEntryDao
    abstract fun planDao(): PlanDao
    abstract fun modelProductionDao(): ModelProductionDao

    companion object {
        @Volatile
        private var INSTANCE: PartDatabase? = null

        fun getRoomDatabase(context: Context): PartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PartDatabase::class.java,
                    "part_database"
                )
                    //.fallbackToDestructiveMigration() // ✅ FIXES crash for now
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


