package com.dark.neuroverse.neurov.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dark.neuroverse.neurov.db.data.ChatMessage
import com.dark.neuroverse.neurov.db.model.ChatDao

@Database(entities = [ChatMessage::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "neuroverse_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
