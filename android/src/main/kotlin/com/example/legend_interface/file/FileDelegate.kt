package com.example.legend_interface.file

import android.Manifest
import android.R
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodChannel


import androidx.core.app.ActivityCompat
import android.net.Uri
import java.io.File


class FileDelegate : io.flutter.plugin.common.PluginRegistry.ActivityResultListener,
    io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener {
    private val TAG = "FilePickerDelegate"
    private val REQUEST_CODE: Int = 1

    private var activity: Activity
    private var permissionManager: PermissionManager
    private  lateinit var pendingResult: MethodChannel.Result
    private lateinit var allowedExtensions: Array<String>
    private var eventSink: EventSink? = null


    constructor(
        activity: Activity

    ) {
        this.activity = activity

        this.permissionManager = object : PermissionManager {
            override fun isPermissionGranted(permissionName: String?): Boolean {
                return (ActivityCompat.checkSelfPermission(activity, permissionName!!)
                        == PackageManager.PERMISSION_GRANTED)
            }

            override fun askForPermission(permissionName: String?, requestCode: Int) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permissionName),
                    requestCode
                )
            }
        }

    }

    fun startFileExplorer() {
        var intent: Intent

        intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        intent.type = "*/*"
        //intent = Intent(Intent.ACTION_OPEN_DOCUMENT)


        if (intent.resolveActivity(this.activity.packageManager) != null) {
            this.activity.startActivityForResult(intent, REQUEST_CODE);
        } else {
            Log.e(
                TAG,
                "Can't find a valid activity to handle the request. Make sure you've a file explorer installed."
            );
            finishWithError("invalid_format_type", "Can't handle the provided file type.");
        }
    }

    fun startFileExplorer(
        result: MethodChannel.Result
    ) {
        this.pendingResult = result
        if (!this.permissionManager.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            this.permissionManager.askForPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                REQUEST_CODE

            )
            return
        }
        startFileExplorer()

    }

    fun setEventHandler(eventSink: EventSink?) {
        this.eventSink = eventSink
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Boolean {

        println(data.dataString)
        println(data.data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {


            val files: ArrayList<FileInfo> = ArrayList()


            // Multiple Files
            if (data.clipData != null) {
                val count: Int = data.clipData!!.itemCount
                var currentItem = 0
                while (currentItem < count) {
                    val currentUri: Uri = data.clipData!!.getItemAt(currentItem).uri

                }

            }// Single File
            else if (data.data != null)  {
                var uri: Uri = data.data!!
                var file:File = File(uri.path)


                var f: FileInfo? = FileUtils.openFileStream(this.activity,uri,true)
                println(f?.name)

                if (f != null) {
                    files.add(f)
                    println(f.size)
                    println(f.name)
                    println(f.bytes)
                    println(f.path)
                    pendingResult.success(f.toMap().toString())
                }






            }



        }


        println(resultCode)
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>?,
        grantResults: IntArray?
    ): Boolean {
        if (REQUEST_CODE != requestCode) {
            return false;
        }

        val permissionGranted: Boolean =
            grantResults?.isNotEmpty()!! && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if (permissionGranted) {
            this.startFileExplorer();
        } else {
            finishWithError(
                "read_external_storage_denied",
                "User did not allowed reading external storage"
            );
        }

        return true;
    }

    private fun finishWithError(errorCode: String, errorMessage: String) {
        if (pendingResult == null) {
            return
        }

        if (eventSink != null) {
            this.dispatchEventStatus(false)
        }
        pendingResult!!.error(errorCode, errorMessage, null)

    }

    private fun dispatchEventStatus(status: Boolean) {
        object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(message: Message?) {
                eventSink!!.success(status)
            }
        }.obtainMessage().sendToTarget()
    }


    interface PermissionManager {
        fun isPermissionGranted(permissionName: String?): Boolean
        fun askForPermission(permissionName: String?, requestCode: Int)
    }
}