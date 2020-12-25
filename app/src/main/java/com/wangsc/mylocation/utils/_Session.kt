package com.wangsc.mylocation.utils

import android.os.Environment
import java.io.File

object _Session {
    var ROOT_DIR = File(Environment.getExternalStorageDirectory().toString() + "/0")
    init {
        if (!ROOT_DIR.exists()) {
            ROOT_DIR.mkdir()
        }
        ROOT_DIR = File(Environment.getExternalStorageDirectory().toString() + "/0/mylocation")
        if (!ROOT_DIR.exists()) {
            ROOT_DIR.mkdir()
        }
    }
}