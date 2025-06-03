package com.dark.plugin_runtime.database.installed_plugin_db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PluginDao{

    @Insert
    suspend fun insertPlugin(plugin: InstalledPluginModel)

    @Query("SELECT * FROM InstalledPluginModel")
    suspend fun getAllPlugins(): List<InstalledPluginModel>

    @Query("DELETE FROM InstalledPluginModel WHERE id = :id")
    suspend fun deletePlugin(id: Int)
}