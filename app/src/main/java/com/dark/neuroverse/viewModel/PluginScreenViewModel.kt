package com.dark.neuroverse.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.dark.plugin_runtime.database.installed_plugin_db.InstalledPluginModel
import com.dark.plugin_runtime.database.installed_plugin_db.PluginInstalledDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PluginScreenViewModel : ViewModel() {
    // StateFlow for plugins list
    private val _pluginsList = MutableStateFlow<List<InstalledPluginModel>>(emptyList())
    val pluginsList: StateFlow<List<InstalledPluginModel>> = _pluginsList


    fun setPluginsList(plugins: List<InstalledPluginModel>) {
        _pluginsList.value = plugins
    }

    fun refreshPlugins(db: PluginInstalledDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            val list = db.pluginDao().getAllPlugins()
            _pluginsList.value = list
        }
    }
}
