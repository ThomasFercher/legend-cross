package com.example.legend_interface

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Build
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger

import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result



/** LegendInterfacePlugin */
class LegendInterfacePlugin : FlutterPlugin, MethodCallHandler, ActivityAware {

  private val TAG = "FilePicker"
  private val CHANNEL = "legend_interface"
  private val EVENT_CHANNEL = "miguelruivo.flutter.plugins.filepickerevent"



  private class LifeCycleObserver internal constructor(private val thisActivity: Activity) : ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {}
    override fun onStart(owner: LifecycleOwner) {}
    override fun onResume(owner: LifecycleOwner) {}
    override fun onPause(owner: LifecycleOwner) {}
    override fun onStop(owner: LifecycleOwner) {
      onActivityStopped(thisActivity)
    }

    override fun onDestroy(owner: LifecycleOwner) {
      onActivityDestroyed(thisActivity)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
      if (thisActivity === activity && activity.applicationContext != null) {
        (activity.applicationContext as Application).unregisterActivityLifecycleCallbacks(this) // Use getApplicationContext() to avoid casting failures
      }
    }

    override fun onActivityStopped(activity: Activity) {}
  }


  private lateinit var lifecycle: Lifecycle
  private lateinit var observer: LifeCycleObserver
  private lateinit var activity: Activity
  private lateinit var activityBinding: ActivityPluginBinding
  private lateinit var channel: MethodChannel
  private lateinit var binaryMessenger: BinaryMessenger

  private lateinit var fileDelegate: FileDelegate
  //private val fileType: String? = null
  //private val isMultipleSelection = false
 // private val withData = false





  override fun onAttachedToEngine(
          @NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding
  ) {
    binaryMessenger = flutterPluginBinding.binaryMessenger
    channel = MethodChannel(binaryMessenger, CHANNEL)
    channel.setMethodCallHandler(this)

    EventChannel(binaryMessenger, EVENT_CHANNEL).setStreamHandler(object : EventChannel.StreamHandler {
      override fun onListen(arguments: Any, events: EventSink) {
        fileDelegate.setEventHandler(events)
      }

      override fun onCancel(arguments: Any) {
        fileDelegate.setEventHandler(null)
      }
    })
    observer = LifeCycleObserver(activity)

      // V2 embedding setup for activity listeners.
      activityBinding.addActivityResultListener(this.delegate)
      activityBinding.addRequestPermissionsResultListener(this.delegate)
      lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(activityBinding)
      lifecycle.addObserver(observer)

  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "getPlatformVersion" -> {
        result.success("Android ${Build.VERSION.RELEASE}")
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onDetachedFromActivity() {
    TODO("Not yet implemented")
  }
}
