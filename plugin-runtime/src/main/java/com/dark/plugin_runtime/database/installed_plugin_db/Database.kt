package com.dark.plugin_runtime.database.installed_plugin_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dark.plugin_runtime.model.PluginModel

@Database(entities = [PluginModel::class], version = 2, exportSchema = true)
@TypeConverters(Converters::class)
abstract class PluginInstalledDatabase : RoomDatabase() {
    abstract fun pluginDao(): PluginDao

    companion object {
        private var INSTANCE: PluginInstalledDatabase? = null

        fun getInstance(context: Context): PluginInstalledDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    PluginInstalledDatabase::class.java,
                    "plugin_database.db"
                ).fallbackToDestructiveMigration(false).build().also { INSTANCE = it }
            }
        }
    }
}