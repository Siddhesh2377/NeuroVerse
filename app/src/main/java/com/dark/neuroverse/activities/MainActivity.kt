package com.dark.neuroverse.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import com.dark.ai_manager.ai.api_calls.AiRouter
import com.dark.neuroverse.BuildConfig
import com.dark.neuroverse.compose.screens.HomeScreen
import com.dark.neuroverse.neurov.mcp.ai.PluginRouter
import com.dark.neuroverse.services.PluginSandboxService
import com.dark.neuroverse.ui.theme.NeuroVerseTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PluginRouter.init(applicationContext)
        AiRouter.initAiRouter(BuildConfig.API_KEY)
        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore

        enableEdgeToEdge()
        setContent {
            NeuroVerseTheme {
                Scaffold {
                    HomeScreen(it)
                    // runPluginInSandbox(this, "List Applications Plugin")
                }
            }
        }
    }

}

fun runPluginInSandbox(context: Context, pluginName: String) {
    val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val messenger = Messenger(service)
            val msg = Message.obtain(null, PluginSandboxService.MSG_RUN_PLUGIN)
            msg.replyTo = Messenger(Handler(Looper.getMainLooper()) { reply ->
                val response = reply.data.getString("status") ?: "unknown"
                Log.i("PluginManager", "🔁 Plugin sandbox response: $response")
                true
            })
            msg.data = Bundle().apply {
                putString("plugin_name", pluginName)
            }
            messenger.send(msg)
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    val intent = Intent(context, PluginSandboxService::class.java)
    context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
}





