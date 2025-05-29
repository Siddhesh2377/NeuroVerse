package com.dark.neuroverse.neurov.mcp.engine

import android.content.Context
import android.util.Log
import com.dark.neuroverse.neurov.Command
import com.dark.neuroverse.neurov.mcp.events.ActionCall
import com.dark.neuroverse.neurov.mcp.model.Action
import com.dark.neuroverse.neurov.mcp.model.Action.OPEN_APP
import com.dark.neuroverse.neurov.mcp.tools.ProgramsTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActionRunner(
    private val command: Command?,
    private val context: Context
) {
    private val action: Action = determineAction()

    fun execute(onResult: (event: ActionCall, data: Any?) -> Unit) {
        when (action) {
            OPEN_APP -> launchApp(onResult)
            Action.NONE -> {
                Log.w("ActionRunner", "No valid action found.")
                onResult(ActionCall.FAILED, null)
            }
        }
    }

    private fun launchApp(onResult: (event: ActionCall, data: Any?) -> Unit) {
        val appName = command?.app.orEmpty()
        if (appName.isBlank()) {
            Log.e("ActionRunner", "App name is missing in command.")
            onResult(ActionCall.FAILED, null)
            return
        }

        Log.d("ActionRunner", "Launching app: $appName")

        CoroutineScope(Dispatchers.Main).launch {
            onResult(ActionCall.STARTED, null)
            val tool = ProgramsTool()
            val appList = tool.listApps(context)

            appList.forEach {
                Log.d("Interpreter", "Available app: ${it.name}")
            }

            val matchedApp = appList.find {
                it.name.contains(appName, ignoreCase = true) || it.packageName.contains(appName.replace(" ", "").lowercase())
            }

            if (matchedApp == null) {
                Log.e("Interpreter", "App not found: $appName")
                onResult(ActionCall.FAILED, null)
                return@launch
            }
            delay(2000)
            tool.launchApp(context, matchedApp.packageName) {
                Log.d("Interpreter", "App launched: ${matchedApp.name}")
                onResult(ActionCall.FINISHED, matchedApp)
            }
        }
    }

    private fun determineAction(): Action {
        return when (command?.action) {
            "open_app" -> OPEN_APP
            else -> Action.NONE
        }
    }
}
