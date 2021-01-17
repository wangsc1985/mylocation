package com.wangsc.mylocation.utils

import com.wangsc.mylocation.e
import com.wangsc.mylocation.utils._Session.ROOT_DIR
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object LoadFileUtils {
    @JvmStatic
    fun loadFileFromHttp(url: String, cacheFileName: String):String {
        val cacheFile = File(ROOT_DIR.absolutePath, cacheFileName)
        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile()
                cacheFile.setWritable(true)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        var uurl: URL? = null
        var output: FileOutputStream? = null
        var inputStream: InputStream? = null
        try {
            uurl = URL(url)
            var connection: HttpURLConnection? = null
            connection = uurl.openConnection() as HttpURLConnection
            connection.setRequestProperty("connection", "Keep-Alive")
            connection.useCaches = false
            connection.readTimeout = 5000
            connection.connectTimeout = 5000
            // connection.setDoOutput(true);
            connection.connect()
            val wdFile = File(cacheFile.path)
            output = FileOutputStream(wdFile)
            inputStream = connection.inputStream
            connection.contentLength
            val data = ByteArray(1024)
            var count = 0
            while (inputStream.read(data).also { count = it } != -1) {
                output.write(data, 0, count)
            }
            output.flush()
        } catch (e: Exception) {
            e(e.message!!)
        } finally {
            try {
                if (inputStream == null) {
                    return ""
                } else {
                    output!!.close()
                    inputStream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return cacheFile.path
        }
    }
}