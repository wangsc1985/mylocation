package com.wangsc.mylocation.utils

//import com.amap.api.services.core.LatLonPoint
//import com.amap.api.services.route.DistanceResult
//import com.amap.api.services.route.DistanceSearch
//import com.amap.api.services.route.DistanceSearch.DistanceQuery

import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.wangsc.mylocation.e
import com.wangsc.mylocation.models.Location
import java.util.*


/**
 *
 */
object AMapUtil {

    val UUID_NULL = UUID.fromString("00000000-0000-0000-0000-000000000000")

    @JvmStatic
    fun getCurrentLocation(context: Context, summary: String, callBack: LocationCallBack) {
        try {//初始化client

            val start = System.currentTimeMillis()
            val locationClient = AMapLocationClient(context)
            val locationOption = AMapLocationClientOption()
            locationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
            locationOption.isGpsFirst = true //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
            locationOption.httpTimeOut = 30000 //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
            locationOption.isOnceLocation = true //可选，设置是否单次定位。默认是false
            locationOption.isOnceLocationLatest = true //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
            locationClient.setLocationOption(locationOption)

            // 设置定位监听
            locationClient.setLocationListener(AMapLocationListener { location ->
                e("获取定位用时：${System.currentTimeMillis() - start}")
                if (null != location) {
                    e("error code : ${location.errorCode}")
                    val sb = StringBuffer()
                    //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                    if (location.errorCode == 0) {
                        e("address : ${location.address}")
                        if (location.accuracy > 100) {
                            return@AMapLocationListener
                        }
                        val l = Location()
                        l.Id = UUID.randomUUID()
                        l.UserId = UUID_NULL
                        l.Time = location.time
                        l.Accuracy = location.accuracy
                        l.AdCode = location.adCode
                        l.Address = location.address
                        l.Bearing = location.bearing
                        l.City = location.city
                        l.CityCode = location.cityCode
                        l.Province = location.province
                        l.Country = location.country
                        l.District = location.district
                        l.LocationType = location.locationType
                        l.Longitude = location.longitude
                        l.PoiName = location.poiName
                        l.Latitude = location.latitude
                        l.Provider = location.provider
                        l.Speed = location.speed
                        l.Satellites = location.satellites
                        l.Summary = summary
                        callBack.OnLocationedListener(l)
                    }
                }
            })
            locationClient.startLocation()
        } catch (e: Exception) {
            _Utils.log2file("err", "运行错误", e.message!!)
            e(e.message!!)
        }
    }

    fun stopLocationContinue() {
        locationClient?.disableBackgroundLocation(true)
        locationClient?.stopLocation()
        locationClient?.onDestroy()
    }

    var locationClient: AMapLocationClient? = null

    @JvmStatic
    fun getLocationContinue(context: Context, summary: String, callBack: LocationCallBack) {
        try {//初始化client
            locationClient = AMapLocationClient(context)
            val locationOption = AMapLocationClientOption()
            locationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
            locationOption.isGpsFirst = true //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
            locationOption.httpTimeOut = 30000 //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
            locationOption.isOnceLocation = false //可选，设置是否单次定位。默认是false
            locationOption.isOnceLocationLatest = true //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
            locationOption.interval = 5000
            locationClient?.setLocationOption(locationOption)
            locationClient?.enableBackgroundLocation(2001, buildNotification(context))

            // 设置定位监听
            locationClient?.setLocationListener(AMapLocationListener { location ->
                if (null != location) {
//                    e("error code : ${location.errorCode}")
                    //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                    if (location.errorCode == 0) {
                        if (location.accuracy > 100) {
                            return@AMapLocationListener
                        }
                        val l = Location()
                        l.Id = UUID.randomUUID()
                        l.UserId = UUID_NULL
                        l.Time = location.time
                        l.Accuracy = location.accuracy
                        l.AdCode = location.adCode
                        l.Address = location.address
                        l.Bearing = location.bearing
                        l.City = location.city
                        l.CityCode = location.cityCode
                        l.Province = location.province
                        l.Country = location.country
                        l.District = location.district
                        l.LocationType = location.locationType
                        l.Longitude = location.longitude
                        l.PoiName = location.poiName
                        l.Latitude = location.latitude
                        l.Provider = location.provider
                        l.Speed = location.speed
                        l.Satellites = location.satellites
                        l.Summary = summary
                        callBack.OnLocationedListener(l)
                    }
                }
            })
            locationClient?.startLocation()
        } catch (e: Exception) {
            _Utils.log2file("err", "运行错误", e.message!!)
            e(e.message!!)
        }
    }

//    @JvmStatic
//    fun getDistants(context: Context?, start: Location, end: Location, callback: DistanceSearchCallback?) {
//        try {
//            val distanceSearch = DistanceSearch(context)
//            distanceSearch.setDistanceSearchListener { distanceResult, i ->
//                callback?.OnDistanceSearchListener(
//                    distanceResult
//                )
//            }
//
//            //设置起点和终点，其中起点支持多个
//            val distanceQuery = DistanceQuery()
//            val latLonPoints: MutableList<LatLonPoint> =
//                ArrayList()
//            latLonPoints.add(
//                LatLonPoint(
//                    start.Latitude,
//                    start.Longitude
//                )
//            )
//            distanceQuery.origins = latLonPoints
//            distanceQuery.destination = LatLonPoint(
//                end.Latitude,
//                end.Longitude
//            )
//            //设置测量方式，支持直线和驾车
//            distanceQuery.type = DistanceSearch.TYPE_DISTANCE
//            distanceSearch.calculateRouteDistanceAsyn(distanceQuery)
//        } catch (ex: Exception) {
//            saveException(context, ex)
//        }
//    }

    interface LocationCallBack {
        fun OnLocationedListener(location: Location)
    }

//    interface DistanceSearchCallback {
//        fun OnDistanceSearchListener(distanceResult: DistanceResult?)
//    }


    private const val NOTIFICATION_CHANNEL_NAME = "BackgroundLocation"
    private var notificationManager: NotificationManager? = null
    var isCreateChannel = false

    @SuppressLint("NewApi")
    private fun buildNotification(context: Context): Notification? {
        var builder: Notification.Builder? = null
        var notification: Notification? = null
        if (Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            }
            if (!isCreateChannel) {
                val notificationChannel = NotificationChannel( "chanel_mylocation", NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT )
                notificationChannel.enableLights(true) //是否在桌面icon右上角展示小圆点
                notificationChannel.lightColor = Color.BLUE //小圆点颜色
                notificationChannel.setShowBadge(true) //是否在久按桌面图标时显示此渠道的通知
                notificationManager!!.createNotificationChannel(notificationChannel)
                isCreateChannel = true
            }
            builder = Notification.Builder(context.getApplicationContext(), "chanel_mylocation")
        } else {
            builder = Notification.Builder(context.getApplicationContext())
        }
        builder!!.setSmallIcon(R.drawable.ic_menu_mylocation)
//            .setContentTitle("阿弥陀佛")
            .setContentText("正在定位...")
            .setWhen(System.currentTimeMillis())
        notification = if (Build.VERSION.SDK_INT >= 16) {
            builder.build()
        } else {
            builder.getNotification()
        }
        return notification
    }
}