package com.wangsc.mylocation.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Application
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.PowerManager.PARTIAL_WAKE_LOCK
import android.os.PowerManager.WakeLock
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wangsc.mylocation.MainActivity
import com.wangsc.mylocation.e
import com.wangsc.mylocation.models.DateTime
import java.io.*
import java.util.*

/**
 * Created by 阿弥陀佛 on 2016/10/18.
 */
object _Utils {

    /**
     * 判断服务是否在运行
     * @param context
     * @param serviceName
     * @return
     * 服务名称为全路径 例如com.ghost.WidgetUpdateService
     */
    fun isRunService(context: Context, serviceName: String): Boolean {
        e("service name : $serviceName")
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceName == service.service.className) {
                return true
            }
        }
        return false
    }
    /**
     * 隐藏虚拟按键，并且全屏
     */
    fun hideBottomUIMenu(activity: Activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            val v = activity.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = activity.window.decorView
            val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
            decorView.systemUiVisibility = uiOptions
        }
    }

    fun isAppRunning(context: Context): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageName = "com.wangsc.lovehome"
        val appProcesses =
            activityManager.runningAppProcesses
        if (appProcesses == null) {
            Log.e("wangsc", "null")
            return false
        }
        for (appProcess in appProcesses) {
            Log.e("wangsc", appProcess.processName)
            if (appProcess.processName == packageName) {
                return true
            }
        }
        return false
    }

    /**
     * 模拟点击HOME按钮
     *
     * @param context
     */
    fun clickHomeButton(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private var textToSpeech: TextToSpeech? = null //创建自带语音对象
    fun speaker(context: Context?, msg: String?) {
        textToSpeech = TextToSpeech(context, OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech!!.setPitch(1.0f) //方法用来控制音调
                textToSpeech!!.setSpeechRate(1.2f) //用来控制语速

                //判断是否支持下面语言
                val result = textToSpeech!!.setLanguage(Locale.CHINA)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "SIMPLIFIED_CHINESE数据丢失或不支持", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    textToSpeech!!.speak(
                        msg,
                        TextToSpeech.QUEUE_FLUSH,
                        null
                    ) //输入中文，若不支持的设备则不会读出来
                }
            }
        })
    }

    fun speaker(
        context: Context?,
        msg: String?,
        pitch: Float
    ) {
        textToSpeech = TextToSpeech(context, OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech!!.setPitch(pitch) //方法用来控制音调
                textToSpeech!!.setSpeechRate(1.2f) //用来控制语速

                //判断是否支持下面语言
                val result = textToSpeech!!.setLanguage(Locale.CHINA)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "SIMPLIFIED_CHINESE数据丢失或不支持", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    textToSpeech!!.speak(
                        msg,
                        TextToSpeech.QUEUE_FLUSH,
                        null
                    ) //输入中文，若不支持的设备则不会读出来
                }
            }
        })
    }

    fun speaker(
        context: Context?,
        msg: String?,
        pitch: Float,
        speech: Float
    ) {
        textToSpeech = TextToSpeech(context, OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech!!.setPitch(pitch) //方法用来控制音调
                textToSpeech!!.setSpeechRate(speech) //用来控制语速

                //判断是否支持下面语言
                val result = textToSpeech!!.setLanguage(Locale.CHINA)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "SIMPLIFIED_CHINESE数据丢失或不支持", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    textToSpeech!!.speak(
                        msg,
                        TextToSpeech.QUEUE_FLUSH,
                        null
                    ) //输入中文，若不支持的设备则不会读出来
                }
            }
        })
    }

    @JvmStatic
    fun printException(context: Context?, e: Exception) {
        try {
            val msg = saveException(context, e)
            AlertDialog.Builder(context).setMessage(msg).setCancelable(false)
                .setPositiveButton("知道了", null).show()
        } catch (e1: Exception) {
        }
    }

    @JvmStatic
    fun saveException(context: Context?, e: Exception): String {
        val msg = getExceptionStr(e)
        log2file("error", "运行错误", msg)
        return msg
    }

    val ROOT_DIR = File(Environment.getExternalStorageDirectory().toString() + "/0/myclock")
    /**
     * 将日志记录到指定文件，文件名{filename}不用添加后缀。
     */
    fun log2file(filename: String, item: String, message: String?) {
        try {

            val logFile = File(ROOT_DIR, "${filename}.log")
            val writer = BufferedWriter(FileWriter(logFile, true))
            writer.write(DateTime().toLongDateTimeString())
            writer.newLine()
            writer.write(item)
            writer.newLine()
            if(message!=null) {
                writer.write(message)
                writer.newLine()
            }
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getExceptionStr(e: Exception): String {
        var msg = ""
        try {
            if (e.stackTrace.size == 0) return ""
            for (ste in e.stackTrace) {
                if (ste.className.contains(MainActivity::class.java.getPackage().name)) {
                    msg += """
                        类名：
                        ${ste.className}
                        方法名：
                        ${ste.methodName}
                        行号：${ste.lineNumber}
                        错误信息：
                        ${e.message}
                        
                        """.trimIndent()
                }
            }
        } catch (exception: Exception) {
        }
        return msg
    }
init {
    if(!ROOT_DIR.exists()){
ROOT_DIR.mkdir()
    }
}
    fun getFilesWithSuffix(
        path: String?,
        suffix: String?
    ): Array<String> {
        val file = File(path)
        val files =
            file.list { dir, name -> if (name != null && name.endsWith(suffix!!)) true else false }
        return files ?: arrayOf()
    }

    var policyManager: DevicePolicyManager? = null
    var componentName: ComponentName? = null

    // 解除绑定
    fun Bind(context: Context) {
        if (componentName != null) {
            policyManager!!.removeActiveAdmin(componentName!!)
            activeManager(context)
        }
    }

    private fun activeManager(context: Context) {
        //使用隐式意图调用系统方法来激活指定的设备管理器
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        Log.e("wangsc", "激活了设备管理器")
    }

    /**
     * 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
     *
     *
     * PARTIAL_WAKE_LOCK :保持CPU 运转，屏幕和键盘灯是关闭的。
     * SCREEN_DIM_WAKE_LOCK ：保持CPU 运转，允许保持屏幕显示但有可能是灰的，关闭键盘灯
     * SCREEN_BRIGHT_WAKE_LOCK ：保持CPU 运转，保持屏幕高亮显示，关闭键盘灯
     * FULL_WAKE_LOCK ：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
     *
     * @param context
     */
    fun acquireWakeLock(context: Context, PowerManager: Int): WakeLock? {
        try {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = pm.newWakeLock(PowerManager, context.javaClass.canonicalName)
            if (null != wakeLock) {
                wakeLock.acquire()
                Log.e("wangsc", "锁定唤醒锁: $wakeLock")
                return wakeLock
                //                    addRunLog2File(context, "", "锁定唤醒锁。");
            }
        } catch (e: Exception) {
            printException(context, e)
        }
        return null
    }
    fun acquireWakeLock(context: Context): WakeLock? {
        return acquireWakeLock(context,PARTIAL_WAKE_LOCK)
    }

    /**
     * 释放设备电源锁
     */
    fun releaseWakeLock(context: Context?, wakeLock: WakeLock?) {
        try {
            Log.e("wangsc", "解除唤醒锁: $wakeLock")
            if (null != wakeLock && wakeLock.isHeld) {
                wakeLock.release()
                //                addRunLog2File(context, "", "解除唤醒锁。");
            }
        } catch (e: Exception) {
            printException(context, e)
        }
    }


    /**
     * 获取所有程序的包名信息
     *
     * @param application
     * @return
     */
    fun getAppInfos(application: Application): List<String> {
        val pm = application.packageManager
        val packgeInfos =
            pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)
        val appInfos: MutableList<String> =
            ArrayList()
        /* 获取应用程序的名称，不是包名，而是清单文件中的labelname
            String str_name = packageInfo.applicationInfo.loadLabel(pm).toString();
            appInfo.setAppName(str_name);
         */for (packgeInfo in packgeInfos) {
//            String appName = packgeInfo.applicationInfo.loadLabel(pm).toString();
            val packageName = packgeInfo.packageName
            //            Drawable drawable = packgeInfo.applicationInfo.loadIcon(pm);
//            AppInfo appInfo = new AppInfo(appName, packageName, drawable);
//            appInfos.add(appInfo);
            appInfos.add(packageName)
        }
        return appInfos
    }

    /**
     * 判断耳机是否连接。
     *
     * @return
     */
    val isHeadsetExists: Boolean
        get() {
            val buffer = CharArray(1024)
            var newState = 0
            try {
                val file = FileReader("/sys/class/switch/h2w/state")
                val len = file.read(buffer, 0, 1024)
                newState = Integer.valueOf(String(buffer, 0, len).trim { it <= ' ' })
            } catch (e: FileNotFoundException) {
                Log.e("FMTest", "This kernel does not have wired headset support")
            } catch (e: Exception) {
                Log.e("FMTest", "", e)
            }
            return newState != 0
        }

    fun isNetworkConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfo = mConnectivityManager.activeNetworkInfo
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable
            }
        }
        return false
    }

    var mWakeLock: WakeLock? = null

    @SuppressLint("InvalidWakeLockTag")
    fun screenOn(context: Context) {
        val pm =
            context.getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "bright"
        )
        mWakeLock!!.acquire(120000)
    }

    fun screenOff(context: Context) {
        val mDevicePolicyManager =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mDevicePolicyManager.lockNow()
    }
}