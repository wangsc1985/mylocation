package com.wangsc.mylocation.sevice

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.annotation.RequiresApi
import com.wangsc.mylocation.R
import com.wangsc.mylocation.e
import com.wangsc.mylocation.models.Location
import com.wangsc.mylocation.phone
import com.wangsc.mylocation.utils.AMapUtil
import com.wangsc.mylocation.utils._CloudUtils
import com.wangsc.mylocation.utils._Utils

class LocationMediaTimerService : Service() {

    private lateinit var mPlayer: MediaPlayer
    private var wakeLock:PowerManager.WakeLock?=null

    var startTimeMillis:Long = 0


    fun playMeida() {
        try {
//            wakeLock = _Utils.acquireWakeLock(applicationContext)
            mPlayer = MediaPlayer.create(applicationContext, R.raw.second_30)
            mPlayer.setVolume(0.01f, 0.01f)
            mPlayer.setLooping(true)
            mPlayer.start()
        } catch (e: Exception) {
            _Utils.printException(applicationContext, e)
        }
    }
    fun stopMedia(){
//        _Utils.releaseWakeLock(applicationContext,wakeLock)
        mPlayer.stop()
        mPlayer.release()
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    var a = 0
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        try {
            startTimeMillis=System.currentTimeMillis()
            AMapUtil.getLocationContinue(applicationContext, "屏幕解锁",
                object : AMapUtil.LocationCallBack {
                    override fun OnLocationedListener(newLocation: Location) {
//                        e(newLocation.Address)
                        // 记录到云数据库
                        if((System.currentTimeMillis()-startTimeMillis)/60000>60){
                            stopService(Intent(applicationContext,LocationMediaTimerService::class.java))
                        }
                        if(a++>5){
                            stopService(Intent(applicationContext,LocationMediaTimerService::class.java))
                        }
                        _CloudUtils.updateLocation(applicationContext, phone, newLocation.Latitude, newLocation.Longitude, newLocation.Address, null)
                    }
                })
            playMeida()
        } catch (e: Exception) {
            _Utils.printException(applicationContext, e)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        e("location timer music service on create...")
    }

    override fun onDestroy() {
        try {
            e("service destory")
            stopMedia()

            AMapUtil.stopLocationContinue()
        } catch (e: Exception) {
        }
        super.onDestroy()
    }
}