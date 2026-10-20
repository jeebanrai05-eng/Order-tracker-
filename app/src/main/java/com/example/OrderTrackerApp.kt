package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class OrderTrackerApp : Application() {
  override fun onCreate() {
    super.onCreate()
    initFirebase()
  }

  private fun initFirebase() {
    try {
      if (FirebaseApp.getApps(this).isEmpty()) {
        val options = FirebaseOptions.Builder()
          .setApplicationId("1:508672273546:android:16dd365c30905ea5c05173")
          .setApiKey("AIzaSyAEFKvjHCP0tggz03iv4ZxViiEXi4zbERA")
          .setProjectId("order-tracking-ec763")
          .setDatabaseUrl("https://order-tracking-ec763-default-rtdb.firebaseio.com")
          .setStorageBucket("order-tracking-ec763.firebasestorage.app")
          .build()
        FirebaseApp.initializeApp(this, options)
        Log.i("OrderTrackerApp", "FirebaseApp initialized programmatically with order-tracking-ec763")
      } else {
        Log.i("OrderTrackerApp", "FirebaseApp already initialized via Provider")
      }
    } catch (e: Exception) {
      Log.e("OrderTrackerApp", "Failed to initialize FirebaseApp", e)
    }
  }
}
