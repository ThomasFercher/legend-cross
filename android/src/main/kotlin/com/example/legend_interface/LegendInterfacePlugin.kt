package com.example.legend_interface

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.legend_interface.file.FileDelegate
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter
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

  private class LifeCycleObserver internal constructor(private val thisActivity: Activity) :
      ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {}
    override fun onStart(owner: LifecycleOwner) {}
    override fun onResume(owner: LifecycleOwner) {}
    override fun onPause(owner: LifecycleOwner) {}
    override fun onStop(owner: LifecycleOwner) {
      this.onActivityStopped(thisActivity)
    }

    override fun onDestroy(owner: LifecycleOwner) {
      this.onActivityDestroyed(thisActivity)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
      if (thisActivity === activity && activity.applicationContext != null) {
        (activity.applicationContext as Application).unregisterActivityLifecycleCallbacks(
            this) // Use getApplicationContext() to avoid casting failures
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
  private lateinit var context: Context


  private lateinit var fileDelegate: FileDelegate
  // private val fileType: String? = null
  // private val isMultipleSelection = false
  // private val withData = false

  override fun onAttachedToEngine(
      @NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding
  ) {
    this.binaryMessenger = flutterPluginBinding.binaryMessenger
    this.context = flutterPluginBinding.applicationContext
    this.channel = MethodChannel(this.binaryMessenger, CHANNEL)
    this.channel.setMethodCallHandler(this)

  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull rawResult: Result) {
    val result: Result = MethodResultWrapper(rawResult)
    val arguments: HashMap<*, *>? = call.arguments as HashMap<*, *>?

    when (call.method) {
      "getPlatformVersion" -> {
        result.success("Android ${Build.VERSION.RELEASE}")
      }
      "pickFile" -> {
        this.fileDelegate.startFileExplorer(result)
      }
      else -> {
        result.error("err", "Alta", null)
      }
    }
  }

  private fun setup(activity: Activity, activityBinding: ActivityPluginBinding) {

    this.fileDelegate = FileDelegate(activity)
    EventChannel(binaryMessenger, EVENT_CHANNEL)
        .setStreamHandler(
            object : EventChannel.StreamHandler {
              override fun onListen(arguments: Any, events: EventSink) {
                fileDelegate.setEventHandler(events)
              }

              override fun onCancel(arguments: Any) {
                fileDelegate.setEventHandler(null)
              }
            })
    observer = LifeCycleObserver(activity)


    // V2 embedding setup for activity listeners.
    activityBinding.addActivityResultListener(this.fileDelegate)
    activityBinding.addRequestPermissionsResultListener(this.fileDelegate)
    lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(activityBinding)
    lifecycle.addObserver(observer)
  }

  private fun tearDown() {
    activityBinding.removeActivityResultListener(this.fileDelegate)
    activityBinding.removeRequestPermissionsResultListener(this.fileDelegate)
    //  activityBinding = null
    if (observer != null) {
      lifecycle.removeObserver(observer)
    }
    //  lifecycle = null
    this.fileDelegate.setEventHandler(null)
    //   this.fileDelegate = null
    channel.setMethodCallHandler(null)
    //  channel = null
    //   this.application = null
  }

  private class MethodResultWrapper internal constructor(private val methodResult: Result) :
      Result {
    private val handler: Handler
    override fun success(result: Any?) {
      handler.post(Runnable { methodResult.success( result as Any) })
    }

    override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
      handler.post(Runnable { methodResult.error(errorCode, errorMessage, errorDetails) })
    }

    override fun notImplemented() {
      handler.post(Runnable { methodResult.notImplemented() })
    }

    init {
      handler = Handler(Looper.getMainLooper())
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    println("test")
    this.activityBinding = binding
    this.activity = binding.activity
    setup(this.activity, this.activityBinding)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    this.onDetachedFromActivity()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    this.onAttachedToActivity(binding)
  }

  override fun onDetachedFromActivity() {
    this.tearDown()
  }
}
