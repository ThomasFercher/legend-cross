package com.example.legend_interface.file

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.*
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*

class FileUtils {



    fun openFileFromURI(uri: Uri) {

    }

    companion object {
        var TAG: String = "LegendFileUtils"

        fun getFileName(uri: Uri, context: Context): String? {
            var result: String? = null
            try {
                if (uri.scheme == "content") {
                    val cursor: Cursor? = context.contentResolver
                        .query(uri, arrayOf<String>(OpenableColumns.DISPLAY_NAME), null, null, null)
                    try {
                        if (cursor != null && cursor.moveToFirst()) {
                            result =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                    } finally {
                        cursor!!.close()
                    }
                }
                if (result == null) {
                    result = uri.path
                    val cut = result!!.lastIndexOf('/')
                    if (cut != -1) {
                        result = result.substring(cut + 1)
                    }
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to handle file name: $ex")
            }
            return result
        }


        fun openFileStream(context: Context, uri: Uri, withData: Boolean): FileInfo? {
            Log.i(TAG, "Caching from URI: $uri")
            var fos: FileOutputStream? = null
            val fileInfo: FileInfo.Builder = FileInfo.Builder()
            val fileName: String = FileUtils.getFileName(uri, context)!!
            val path =
                context.cacheDir.absolutePath + "/legend_files/" + (fileName ?: Random().nextInt(
                    100000
                ))
            val file = File(path)
            if (file.exists() && withData) {
                val size = file.length().toInt()
                val bytes = ByteArray(size)
                try {
                    val buf = BufferedInputStream(FileInputStream(file))
                    buf.read(bytes, 0, bytes.size)
                    buf.close()
                } catch (e: FileNotFoundException) {
                    Log.e(TAG, "File not found: " + e.message, null)
                } catch (e: IOException) {
                    Log.e(TAG, "Failed to close file streams: " + e.message, null)
                }
                fileInfo.withData(bytes)
            } else {
                file.getParentFile().mkdirs()
                try {
                    fos = FileOutputStream(path)
                    try {
                        val out = BufferedOutputStream(fos)
                        val `in`: InputStream = context.contentResolver.openInputStream(uri)!!
                        val buffer = ByteArray(8192)
                        var len = 0
                        while (`in`.read(buffer).also({ len = it }) >= 0) {
                            out.write(buffer, 0, len)
                        }
                        if (withData) {
                            try {
                                var fis: FileInputStream? = null
                                val bytes = ByteArray(file.length() as Int)
                                fis = FileInputStream(file)
                                fis.read(bytes)
                                fis.close()
                                fileInfo.withData(bytes)
                            } catch (e: Exception) {
                                Log.e(
                                    TAG,
                                    "Failed to load bytes into memory with error $e. Probably the file is too big to fit device memory. Bytes won't be added to the file this time."
                                )
                            }
                        }
                        out.flush()
                    } finally {
                        fos.getFD().sync()
                    }
                } catch (e: Exception) {
                    try {
                        fos?.close()
                    } catch (ex: IOException) {
                        Log.e(TAG, "Failed to close file streams: " + e.message, null)
                        return null
                    } catch (ex: NullPointerException) {
                        Log.e(TAG, "Failed to close file streams: " + e.message, null)
                        return null
                    }
                    Log.e(TAG, "Failed to retrieve path: " + e.message, null)
                    return null
                }
            }
            Log.d(TAG, "File loaded and cached at:$path")
            fileInfo
                .withPath(path)
                .withName(fileName)
                .withSize(java.lang.String.valueOf(file.length()).toLong())
            return fileInfo.build()
        }

    }
}