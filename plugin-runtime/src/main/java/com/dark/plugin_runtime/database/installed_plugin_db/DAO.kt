package com.dark.plugin_runtime.database.installed_plugin_db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.io.File

@Dao
interface PluginDao{

    @Insert
    suspend fun insertPlugin(plugin: InstalledPluginModel)

    @Query("SELECT * FROM InstalledPluginModel")
    suspend fun getAllPlugins(): List<InstalledPluginModel>

    @Query("SELECT pluginPath FROM InstalledPluginModel WHERE pluginName = :name")
    suspend fun getPluginFolderByName(name: String): String

    @Query("SELECT mainClass FROM InstalledPluginModel WHERE pluginName = :name")
    suspend fun getMainClassByName(name: String): String

    @Query("SELECT pluginDescription FROM InstalledPluginModel WHERE pluginName = :name")
    suspend fun getPluginDescriptionByName(name: String): String

    @Query("DELETE FROM InstalledPluginModel WHERE id = :id")
    suspend fun deletePlugin(id: Int)
}