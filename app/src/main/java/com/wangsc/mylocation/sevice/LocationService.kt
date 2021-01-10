package com.wangsc.mylocation.sevice

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.wangsc.mylocation.R
import com.wangsc.mylocation.e
import com.wangsc.mylocation.models.Location
import com.wangsc.mylocation.phone
import com.wangsc.mylocation.utils.AMapUtil
import com.wangsc.mylocation.utils._CloudUtils
import com.wangsc.mylocation.utils._Utils

/**
 * 服务常住的方法：MediaPlayer循环，Notification通知
 */
class LocationService : Service() {

    private lateinit var mPlayer: MediaPlayer
    private  var isAutoClose=true

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
            isAutoClose = intent.getBooleanExtra("isAutoClose",true)
            e("location is auto close : $isAutoClose")
            startTimeMillis=System.currentTimeMillis()
            AMapUtil.getLocationContinue(applicationContext, "屏幕解锁",
                object : AMapUtil.LocationCallBack {
                    override fun OnLocationedListener(newLocation: Location) {
//                        e(newLocation.Address)
                        // 记录到云数据库
                        if(isAutoClose&&(System.currentTimeMillis()-startTimeMillis)/60000>60){
                            stopSelf()
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