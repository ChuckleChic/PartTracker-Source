package com.example.parttracker

import android.app.Application
import androidx.room.Room
import com.example.parttracker.data.PartDatabase


class MyApplication : Application() {
    companion object {
        lateinit var database: PartDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            PartDatabase::class.java,
            "part_database"
        )
            .fallbackToDestructiveMigration() // Wipes old data on version change
            .build()
    }
}
