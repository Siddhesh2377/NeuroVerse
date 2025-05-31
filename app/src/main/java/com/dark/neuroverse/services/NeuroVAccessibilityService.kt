//package com.dark.neuroverse.services
//
//import android.accessibilityservice.AccessibilityService
//import android.accessibilityservice.AccessibilityServiceInfo
//import android.util.Log
//import android.view.accessibility.AccessibilityEvent
//
//
//class NeuroVAccessibilityService : AccessibilityService() {
//
//    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
//        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
//            Log.d("MyService", "View clicked")
//        }
//
//        // You can’t directly get scancodes here — but we’ll get around that!
//    }
//
//    override fun onServiceConnected() {
//        Log.d("MyService", "Accessibility service connected")
//        this.serviceInfo = this.serviceInfo.apply {
//            flags = flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
//        }
//
//
//    }
//
////    override fun onKeyEvent(event: KeyEvent): Boolean {
////        Log.d("MyService", "Key event received: ${event.scanCode}, keyCode=${event.keyCode}")
////
////        if (event.scanCode == 250 && event.action == KeyEvent.ACTION_DOWN) {
////            val intent = Intent(this, AssistantActivity::class.java).apply {
////                flags = Intent.FLAG_ACTIVITY_NEW_TASK
////            }
////            startActivity(intent)
////            Log.d("MyService", "🔥 Essential Button Pressed! Assistant Launched")
////            return true
////        }
////
////        return super.onKeyEvent(event)
////    }
//
//
//    override fun onInterrupt() {}
//}