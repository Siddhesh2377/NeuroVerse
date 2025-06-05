package com.dark.plugin_runtime.database.installed_plugin_db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.io.File

@Dao
interface PluginDao{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlugin(plugin: InstalledPluginModel): Long

    @Query("SELECT * FROM InstalledPluginModel")
    suspend fun getAllPlugins(): List<InstalledPluginModel>

    @Query("SELECT pluginPath FROM InstalledPluginModel WHERE pluginName = :name")
    suspend fun getPluginFolderByName(name: String): String

    @Query("SELECT mainClass FROM InstalledPluginModel WHERE pluginName = :name")
    suspend fun getMainClassByName(name: String): String

    @Query("SELECT pluginDescription FROM InstalledPluginModel WHERE pluginName = :name")
    suspend fun getPluginDescriptionByName(name: String): String

    @Query("UPDATE InstalledPluginModel SET isEnabled = :enabled WHERE id = :pluginId")
    suspend fun updatePluginEnabled(pluginId: Int, enabled: Boolean)

    @Query("DELETE FROM InstalledPluginModel WHERE id = :id")
    suspend fun deletePlugin(id: Int)
}