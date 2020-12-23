package com.wangsc.mylocation.utils

import android.os.Environment
import com.wangsc.mylocation.callbacks.HttpCallback
import com.wangsc.mylocation.e
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object LoadFileUtils {
    @JvmStatic
    fun loadFileFromHttp(url: String?, cacheFileName: String?):String {
        val ROOT_DIR = File(Environment.getExternalStorageDirectory().toString() + "/0/mylocation")
        if (!ROOT_DIR.exists()) {
            ROOT_DIR.mkdir()
        }
        val cacheFile = File(ROOT_DIR.absolutePath, cacheFileName)
        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile()
                cacheFile.setWritable(true)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        var u: URL? = null
        var rtn = false
        var output: FileOutputStream? = null
        var `is`: InputStream? = null
        try {
            u = URL(url)
            var connection: HttpURLConnection? = null
            connection = u.openConnection() as HttpURLConnection
            connection.setRequestProperty("connection", "Keep-Alive")
            // connection.setRequestMethod("POST");
            connection!!.useCaches = false
            // connection.setRequestProperty("Content-Type",
            // "application/json");
            // connection.setDoInput(true);
            connection.readTimeout = 5000
            connection.connectTimeout = 5000
            // connection.setDoOutput(true);
            connection.connect()
            //            if (!TextUtils.isEmpty(content)) {
//                OutputStream outputStream = connection.getOutputStream();
//                outputStream.write(content.getBytes());
//                outputStream.close();
//            }
            val wdFile = File(cacheFile.path)
            output = FileOutputStream(wdFile)
            `is` = connection.inputStream
            connection.contentLength
            val data = ByteArray(1024)
            var count = 0
            while (`is`.read(data).also { count = it } != -1) {
                output.write(data, 0, count)
            }
            output.flush()
        } catch (e: Exception) {
            e(e.message!!)
        } finally {
            try {
                if (`is` == null) {
                    return ""
                } else {
                    output!!.close()
                    `is`.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return cacheFile.path
        }
    }
}