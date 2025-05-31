package com.dark.neuroverse.services

import android.content.Context
import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService
import android.util.Log
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.dark.neuroverse.compose.screens.assistant.AssistantScreen

class NeuroVoiceInteractionSessionService : VoiceInteractionSessionService() {
    override fun onNewSession(args: Bundle?): VoiceInteractionSession {
        Log.d("NeuroSessionService", "onNewSession called")
        return NeuroSession(this)
    }
}

class NeuroSession(context: Context) : VoiceInteractionSession(context) {

    private lateinit var lifecycleOwner: FakeLifecycleOwner
    private lateinit var savedStateRegistryOwner: FakeSavedStateRegistryOwner

    override fun onCreate() {
        super.onCreate()
        Log.d("NeuroSession", "onCreate: Creating Compose UI inside VoiceInteractionSession")

        lifecycleOwner = FakeLifecycleOwner()
        savedStateRegistryOwner = FakeSavedStateRegistryOwner(lifecycleOwner)
        savedStateRegistryOwner.performRestore(null)

        val root = FrameLayout(context).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
        }

        val composeView = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                AssistantScreen(
                    onClickOutside = {
                        Log.d("NeuroSession", "AssistantScreen onClickOutside, finishing session.")
                        finish() // This is for the grey area click
                    },
                    onActionCompleted = { // Pass the new callback
                        Log.d("NeuroSession", "Action completed, finishing session.")
                        finish()
                    }
                )
            }
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        root.addView(composeView)
        setContentView(root)

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        Log.d("NeuroSession", "onCreate: Compose UI created.")
    }

    // ... (onShow, onHide, onDestroy remain the same) ...

    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)
        Log.d("NeuroSession", "onShow: Voice interaction UI shown. Moving Lifecycle to RESUMED.")
        if (::lifecycleOwner.isInitialized) {
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    override fun onHide() {
        super.onHide()
        Log.d("NeuroSession", "onHide: Voice interaction UI hidden. Moving Lifecycle to PAUSED.")
        if (::lifecycleOwner.isInitialized) {
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
    }

    override fun onDestroy() {
        Log.d("NeuroSession", "onDestroy: Destroying session. Moving Lifecycle to DESTROYED.")
        if (::lifecycleOwner.isInitialized) {
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }
        super.onDestroy()
    }
}

// FakeLifecycleOwner and FakeSavedStateRegistryOwner remain the same
// ... (FakeLifecycleOwner and FakeSavedStateRegistryOwner code)
class FakeLifecycleOwner : LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        Log.d("FakeLifecycleOwner", "Lifecycle initialized.")
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
        Log.d(
            "FakeLifecycleOwner",
            "Lifecycle event: $event, Current state: ${lifecycleRegistry.currentState}"
        )
    }
}

class FakeSavedStateRegistryOwner(
    private val lifecycleOwnerForState: LifecycleOwner
) : SavedStateRegistryOwner {
    private val controller = SavedStateRegistryController.create(this)

    override val savedStateRegistry: SavedStateRegistry
        get() = controller.savedStateRegistry

    override val lifecycle: Lifecycle
        get() = lifecycleOwnerForState.lifecycle

    init {
        Log.d(
            "FakeSavedStateRegistryOwner",
            "SavedStateRegistryOwner initialized. Controller will observe its lifecycle: ${this.lifecycle.currentState}"
        )
    }

    fun performRestore(savedState: Bundle?) {
        controller.performRestore(savedState)
        Log.d(
            "FakeSavedStateRegistryOwner",
            "performRestore explicitly called with bundle: $savedState. Lifecycle state: ${lifecycle.currentState}"
        )
    }

    @Deprecated("performSave is typically handled by the controller observing the lifecycle.")
    fun performSave(outBundle: Bundle) {
        controller.performSave(outBundle)
        Log.d("FakeSavedStateRegistryOwner", "performSave explicitly called.")
    }
}