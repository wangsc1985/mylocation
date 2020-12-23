package com.wangsc.mylocation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wangsc.mylocation.utils.AMapUtil
import com.wangsc.mylocation.models.Location
import com.wangsc.mylocation.utils._CloudUtils
import com.wangsc.mylocation.utils._Utils
import com.wangsc.mylocation.e
import com.wangsc.mylocation.phone

class ScreenBroadcaseReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_USER_PRESENT -> try {
                e("my location 屏幕解锁")
                val now = System.currentTimeMillis()
                if (now - preDateTime >= 10000) {
                    try {
                        AMapUtil.getCurrentLocation(context, "屏幕解锁", object : AMapUtil.LocationCallBack {
                            override fun OnLocationedListener(newLocation: Location) {
                                // 记录到云数据库
                                e("位置：${newLocation.Address}")
                                _CloudUtils.addLocation(context,phone, newLocation.Latitude, newLocation.Longitude, newLocation.Address, null)
                            }
                        })
                    } catch (e: Exception) {
                        _Utils.saveException(context, e)
                    } finally {
                        preDateTime = now
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private var preDateTime: Long = 0
    }
}