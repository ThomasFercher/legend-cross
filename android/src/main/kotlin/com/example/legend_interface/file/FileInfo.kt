package com.example.legend_interface.file

import java.util.*
import kotlin.collections.HashMap

class FileInfo {

     var path: String?
     var name: String?
     var bytes: ByteArray?
     var size: Long

    constructor(
        bytes: ByteArray, size: Long, name: String?, path: String?
    ) {
        this.path = path
        this.bytes = bytes
        this.size = size
        this.name = name
    }

    class Builder {
        private var path: String? = null
        private var name: String? = null
        private var size: Long = 0
        private lateinit var bytes: ByteArray
        fun withPath(path: String?): Builder {
            this.path = path
            return this
        }

        fun withName(name: String?): Builder {
            this.name = name
            return this
        }

        fun withSize(size: Long): Builder {
            this.size = size
            return this
        }

        fun withData(bytes: ByteArray): Builder {
            this.bytes = bytes
            return this
        }

        fun build(): FileInfo {
            return FileInfo(bytes, size, name, path)
        }
    }

    fun toMap(): HashMap<String, Any?> {
        val data: HashMap<String, Any?> = HashMap()
        data["path"] = path
        data["name"] = name
        data["size"] = size
        data["bytes"] = bytes
        return data
    }
}