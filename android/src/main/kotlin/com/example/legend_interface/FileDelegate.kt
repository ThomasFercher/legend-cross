package com.example.legend_interface

import android.app.Activity
import android.content.Intent
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodChannel


class FileDelegate : io.flutter.plugin.common.PluginRegistry.ActivityResultListener, io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener {
    private val TAG = "FilePickerDelegate"
    //private val REQUEST_CODE: Int = FilePickerPlugin::class.java.hashCode() + 43 and 0x0000ffff

    private lateinit var activity: Activity
 //   private lateinit var permissionManager: PermissionManager
    private lateinit var pendingResult: MethodChannel.Result
    private lateinit var allowedExtensions: Array<String>
    private lateinit var eventSink: EventSink



    fun setEventHandler(eventSink: EventSink) {
        this.eventSink = eventSink
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?): Boolean {
        TODO("Not yet implemented")
    }

    internal interface PermissionManager {
        fun isPermissionGranted(permissionName: String?): Boolean
        fun askForPermission(permissionName: String?, requestCode: Int)
    }
}