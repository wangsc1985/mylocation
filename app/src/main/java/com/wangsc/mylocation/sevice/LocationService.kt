package com.wangsc.mylocation.sevice

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.annotation.RequiresApi
import com.wangsc.mylocation.R
import com.wangsc.mylocation.SetNotificationViews
import com.wangsc.mylocation.e
import com.wangsc.mylocation.models.DateTime
import com.wangsc.mylocation.models.Location
import com.wangsc.mylocation.models.LocationState
import com.wangsc.mylocation.models.LocationMessage
import com.wangsc.mylocation.phone
import com.wangsc.mylocation.utils.AMapUtil
import com.wangsc.mylocation.utils._CloudUtils
import com.wangsc.mylocation.utils._NotificationUtils
import com.wangsc.mylocation.utils._Utils
import org.greenrobot.eventbus.EventBus
import java.text.DecimalFormat

/**
 * 服务常住的方法：MediaPlayer循环，Notification通知
 */
class LocationService : Service() {

    private var wakeLock:PowerManager.WakeLock?=null
    private lateinit var mPlayer: MediaPlayer
    private var isAutoClose = true

    var startTimeMillis: Long = 0
    var prvTimeMillis:Long = 0

    fun playMeida() {
        try {
            wakeLock = _Utils.acquireWakeLock(applicationContext)
            mPlayer = MediaPlayer.create(applicationContext, R.raw.second_30)
            mPlayer.setVolume(0.01f, 0.01f)
            mPlayer.setLooping(false)
            mPlayer.setOnCompletionListener {
                _NotificationUtils.sendNotification(1011,applicationContext,R.layout.notification1, SetNotificationViews {
                    val duration = System.currentTimeMillis()-startTimeMillis
                    var minite = duration%(60000*60)/60000
                    val ms = if(minite<10) "0"+minite else minite
                    var hour = duration/60000/60
                    it.setTextViewText(R.id.tv_title,"${hour}:${ms}");
                })
                it.start()
            }
            mPlayer.start()

            _NotificationUtils.sendNotification(1011,applicationContext,R.layout.notification1, SetNotificationViews {
                val duration = System.currentTimeMillis()-startTimeMillis
                var minite = duration%(60000*60)/60000
                val ms = if(minite<10) "0"+minite else minite
                var hour = duration/60000/60
                it.setTextViewText(R.id.tv_title,"${hour}:${ms}");
            })
        } catch (e: Exception) {
            _Utils.printException(applicationContext, e)
        }
    }

    fun stopMedia() {
        _NotificationUtils.closeNotification(applicationContext,1011)
        _Utils.releaseWakeLock(applicationContext,wakeLock)
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
            EventBus.getDefault().post(LocationState.getInstance(true))
            isAutoClose = intent.getBooleanExtra("isAutoClose", true)
            startTimeMillis = System.currentTimeMillis()
            AMapUtil.getLocationContinue(applicationContext, "屏幕解锁",
                object : AMapUtil.LocationCallBack {
                    override fun OnLocationedListener(newLocation: Location) {
                        e(newLocation.address)
                        // 记录到云数据库
                        val now = System.currentTimeMillis()
                        if (isAutoClose && (now - startTimeMillis) / 60000 > 60) {
                            stopSelf()
                        }
                        val delay = (DateTime().timeInMillis - newLocation.time) / 1000
                        EventBus.getDefault().post(
                            LocationMessage.getInstance(delay,newLocation.accuracy,
                                "${delay}s  " +
                                        "${DecimalFormat("0").format(newLocation.accuracy)}m  " +
                                        "${DecimalFormat("0").format(newLocation.speed)}m/s  " +
                                        "${DecimalFormat("0.0").format(newLocation.bearing)}°"
                            )
                        )
                        _CloudUtils.updateLocation(applicationContext, phone, newLocation, null)
                        if(prvTimeMillis!=0L&&now-prvTimeMillis>20000){
                            _Utils.speaker(applicationContext,"${(now-prvTimeMillis)/1000}秒")
                        }
                        prvTimeMillis = now
                    }
                })
            playMeida()
        } catch (e: Exception) {
            _Utils.printException(applicationContext, e)
            e(e.message!!)
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
            EventBus.getDefault().post(LocationState.getInstance(false))
            stopMedia()

            AMapUtil.stopLocationContinue()
        } catch (e: Exception) {
        }
        super.onDestroy()
    }
}