package com.wangsc.mylocation

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.*
import com.amap.api.maps.AMap.CancelableCallback
import com.amap.api.maps.model.*
import com.amap.api.maps.model.LatLngBounds
import com.amap.api.services.share.ShareSearch
import com.wangsc.mylocation.callbacks.CloudCallback
import com.wangsc.mylocation.models.DataContext
import com.wangsc.mylocation.models.Setting
import com.wangsc.mylocation.models.User
import com.wangsc.mylocation.sevice.LocationMediaTimerService
import com.wangsc.mylocation.utils.ImageUtils
import com.wangsc.mylocation.utils.LoadFileUtils
import com.wangsc.mylocation.utils._CloudUtils
import com.wangsc.mylocation.utils._Utils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private val MY_PERMISSIONS_REQUEST: Int = 100
    private var locationIsOn = false
    private var users: MutableList<User>? = null
    private var showType = 2
    private var targetUserName = ""

    //region 动态权限申请
    var permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    var mNoPassedPermissionList: MutableList<String> = ArrayList()

    /**
     * 申请权限
     */
    fun requestPermission(): Boolean {
        mNoPassedPermissionList.clear()
        for (i in permissions.indices) {
            if (ContextCompat.checkSelfPermission(this@MainActivity, permissions.get(i)) != PackageManager.PERMISSION_GRANTED) {
                e("权限名称 : ${permissions[i]} , 返回结果 : 未授权")
                mNoPassedPermissionList.add(permissions.get(i))
            }
        }
        if (mNoPassedPermissionList.isEmpty()) {
            return true
        } else {
            //请求权限方法
            val permissions = mNoPassedPermissionList.toTypedArray()
            ActivityCompat.requestPermissions(this@MainActivity, permissions, MY_PERMISSIONS_REQUEST)
            return false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // 判断 是否仍然继续可以申请权限
                    val showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, permissions[i])
                    e("权限名称：${permissions[i]}，申请结果：${grantResults[i]}，是否可再次申请：${showRequestPermission}")
//                    if (!showRequestPermission) {
//                        AlertDialog.Builder(this).setMessage("有权限未授权，且被禁止申请，请手动授权。").setNegativeButton("知道了", DialogInterface.OnClickListener { dialog, which ->
//                            this.finish()
//                        }).show()
//                    }else{
                    AlertDialog.Builder(this).setMessage("授权失败").setNegativeButton("知道了", DialogInterface.OnClickListener { dialog, which ->
                        this.finish()
                    }).show()
//                    }
                    return
                }
            }

            initOnCreate()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    //endregion


    fun addUserView(name: String, avatarImg: Bitmap, time: String): View {
        val view = View.inflate(this, R.layout.inflate_location_user, null)
        val avatarView = view.findViewById<ImageView>(R.id.iv_avatar)
        val timeView = view.findViewById<TextView>(R.id.tv_time)

        avatarView.setOnClickListener {
            targetUserName = name
            showType = 1
            moveMarks()
//            iv_showAll.setImageResource(R.drawable.show_all_off)
            tv_showAll.setTextColor(Color.BLACK)
        }

        avatarView.setImageBitmap(avatarImg)


        runOnUiThread {
            timeView.setText(time)
            layout_users.addView(view)
        }
        return view
    }

    fun updateUserView(name: String, view: View, time: String) {
        val timeView = view.findViewById<TextView>(R.id.tv_time)
        runOnUiThread {
            if (showType == 1) {
                if (name == targetUserName) {
                    timeView.setTextColor(Color.RED)
                } else {
                    timeView.setTextColor(Color.BLACK)
                }
            }
            timeView.setText(time)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textureMapView.onCreate(savedInstanceState)
        e(Abc.sHA1(this))

        /**
         * 如果全部权限授权通过，直接运行初始化方法。
         * 如果有权限未授权，去申请权限，根据授权结果，决定是否进行初始化方法
         */
        if (requestPermission() == true) {
            initOnCreate()
        }
    }

    private fun isNotifyAllowed(): Boolean {
        val manager = NotificationManagerCompat.from(this)
        // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
        return manager.areNotificationsEnabled()
    }

    private fun openNotifySetting() {
        val intent = Intent()
        try {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            //8.0及以后版本使用这两个extra.  >=API 26
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, applicationInfo.uid)
            //5.0-7.1 使用这两个extra.  <= API 25, >=API 21
            intent.putExtra("app_package", packageName)
            intent.putExtra("app_uid", applicationInfo.uid)

            startActivity(intent)
        } catch (e: Exception) {
            //其他低版本或者异常情况，走该节点。进入APP设置界面
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.putExtra("package", packageName)
            //val uri = Uri.fromParts("package", packageName, null)
            //intent.data = uri
            startActivity(intent)
        }

    }

    private fun initOnCreate() {
        val dc = DataContext(this)
        val settingPhone = dc.getSetting(Setting.KEYS.phone)
        val settingTeamCode = dc.getSetting(Setting.KEYS.team_code)
        if (settingPhone != null && settingTeamCode != null) {
            phone = settingPhone.string
            teamCode = settingTeamCode.string
        }


        if (phone.isEmpty() || teamCode.isEmpty()) {
            val view = View.inflate(this, R.layout.dialog_team, null)
            val phoneView = view.findViewById<EditText>(R.id.et_phone)
            val teamCodeView = view.findViewById<EditText>(R.id.et_teamCode)
            AlertDialog.Builder(this).setTitle("用户信息").setIcon(android.R.drawable.ic_dialog_info)
                .setView(view).setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                    phone = phoneView.text.toString()
                    teamCode = teamCodeView.text.toString()
                    dc.editSetting(Setting.KEYS.phone, phone)
                    dc.editSetting(Setting.KEYS.team_code, teamCode)
                    initView(dc)
                }).setNegativeButton("取消", null).show();
        } else {
            initView(dc)
        }
    }

    private fun initView(dc: DataContext) {
        try {
            initMap()
            initMarks()

            startTimer()
            when (showType) {
                0 -> {
                    //                    iv_showAll.setImageResource(R.drawable.show_all_off)
                    tv_showAll.setTextColor(Color.BLACK)
                }
                1 -> {
                    //                    iv_showAll.setImageResource(R.drawable.show_all_off)
                    tv_showAll.setTextColor(Color.BLACK)
                }
                2 -> {
                    //                    iv_showAll.setImageResource(R.drawable.show_all_on)
                    tv_showAll.setTextColor(Color.RED)
                }
            }

            if (_Utils.isRunService(this, "com.wangsc.mylocation.sevice.LocationMediaTimerService")) {
                iv_share.setImageResource(R.drawable.share_on)
                tv_share.setTextColor(Color.RED)
                locationIsOn = true
            }
            layout_share.setOnClickListener {
                if (!isNotifyAllowed()) {
                    openNotifySetting()
                }else{
                    if (!locationIsOn) {
                        startService(Intent(this, LocationMediaTimerService::class.java))
                        iv_share.setImageResource(R.drawable.share_on)
                        tv_share.setTextColor(Color.RED)
                        locationIsOn = true
                    } else {
                        stopService(Intent(this, LocationMediaTimerService::class.java))
                        iv_share.setImageResource(R.drawable.share_off)
                        tv_share.setTextColor(Color.BLACK)
                        locationIsOn = false
                    }
                }
            }
            layout_share.setOnLongClickListener {
                val view = View.inflate(this, R.layout.dialog_team, null)
                val phoneView = view.findViewById<EditText>(R.id.et_phone)
                val teamCodeView = view.findViewById<EditText>(R.id.et_teamCode)
                AlertDialog.Builder(this).setTitle("用户信息").setIcon(android.R.drawable.ic_dialog_info)
                    .setView(view).setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        phone = phoneView.text.toString()
                        teamCode = teamCodeView.text.toString()
                        dc.editSetting(Setting.KEYS.phone, phone)
                        dc.editSetting(Setting.KEYS.team_code, teamCode)
                    }).setNegativeButton("取消", null).show();
                true
            }

            layout_showAll.setOnClickListener {
                showType = 2
                //                iv_showAll.setImageResource(R.drawable.show_all_on)
                tv_showAll.setTextColor(Color.RED)
                moveMarks()
                users?.forEach {
                    it.view.findViewById<TextView>(R.id.tv_time).setTextColor(Color.BLACK)
                }
            }
        } catch (e: Exception) {
            e(e.message!!)
        }
    }

    fun moveMarks() {
        _CloudUtils.getLocations(this, teamCode, object : CloudCallback {
            override fun excute(code: Int, result: Any?) {
                if (code == 0) {
                    try {
                        var models = result as MutableList<User>
                        var latlngs: MutableList<LatLng> = ArrayList()
                        var myLatlng: LatLng? = null
//                        e(models.size)
                        models.forEach {
                            if (targetUserName.isEmpty()) {
                                if (it.phone == phone) {
                                    targetUserName = it.name
                                    myLatlng = LatLng(it.latitude, it.longitude)
                                }
                            } else {
                                if (targetUserName == it.name) {
                                    myLatlng = LatLng(it.latitude, it.longitude)
                                }
                            }
                            latlngs.add(LatLng(it.latitude, it.longitude))
                            for (i in 0..users!!.size) {
                                try {
                                    var user = users!![i]
                                    if (user.name == it.name) {
                                        user.address = it.address
                                        user.latitude = it.latitude
                                        user.longitude = it.longitude
                                        user.locationTime = it.locationTime
//                                        e("${it.locationTime.toShortTimeString()}  ${it.name}\t[ ${it.latitude} , ${it.longitude} ]")

                                        val span = (System.currentTimeMillis() - it.locationTime.timeInMillis) / 1000
                                        var time = ""
                                        time = span2time(span)
                                        if (user.locationMarker != null && user.avatarMarker != null) {
                                            updateUserView(user.name, user.view, time)
                                            moveMarker(user.locationMarker, it.latitude, it.longitude)
                                            moveMarker(user.avatarMarker, it.latitude, it.longitude)
                                        }
                                        break;
                                    }
                                } catch (e: Exception) {
                                    e(e.message!!)
                                }
                            }
                        }

                        when (showType) {
                            1 -> {
                                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(myLatlng, zoom, 0f, 0f)), 100, null)
                            }
                            2 -> {
                                updateBounds(latlngs)
                            }
                        }
                    } catch (e: Exception) {
                        e(e.message!!)
                    }
                }
            }
        })
    }

    private fun span2time(span: Long): String {
        var time1 = ""

        if (span > 60 * 60 * 24 * 365) {
            time1 = "${span / (60 * 60 * 24 * 365)}年前"
        } else if (span > 60 * 60 * 24 * 30) {
            time1 = "${span / (60 * 60 * 24 * 30)}月前"
        } else if (span > 60 * 60 * 24) {
            time1 = "${span / (60 * 60 * 24)}天前"
        } else if (span > 60 * 60) {
            time1 = "${span / (60 * 60)}小时前"
        } else if (span > 60) {
            time1 = "${span / 60}分钟前"
        } else if (span > 10) {
            time1 = "${span}秒前"
        } else {
            time1 = "刚刚"
        }
        return time1
    }

    fun initMarks() {
        _CloudUtils.getLocations(this, teamCode, object : CloudCallback {
            override fun excute(code: Int, result: Any?) {
                if (code == 0) {
                    try {
                        users = result as MutableList<User>
                        var latlngs: MutableList<LatLng> = ArrayList()
                        var myLatlng: LatLng? = null

                        users?.forEach {
                            try {
                                if (targetUserName.isEmpty()) {
                                    if (it.phone == phone) {
                                        targetUserName = it.name
                                        myLatlng = LatLng(it.latitude, it.longitude)
                                    }
                                } else {
                                    if (targetUserName == it.name) {
                                        myLatlng = LatLng(it.latitude, it.longitude)
                                    }
                                }
                                latlngs.add(LatLng(it.latitude, it.longitude))


                                var avatarUrl = "";
//                                val latcha = CountDownLatch(1)
                                _CloudUtils.getDownLoadPath(this@MainActivity, it.avatar, CloudCallback { code, result ->
                                    try {
                                        if (code == 0) {
                                            avatarUrl = result.toString()
                                            var url = LoadFileUtils.loadFileFromHttp(avatarUrl, "${it.name}.jpg")
                                            if (!url.isEmpty()) {
                                                val avatar = BitmapFactory.decodeFile(url)
                                                val span = (System.currentTimeMillis() - it.locationTime.timeInMillis) / 1000
                                                var time = ""
                                                time = span2time(span)

                                                it.view = addUserView(it.name, avatar, time)
                                                it.locationMarker = addLocationMarkers(it.locationTime.toTimeString(), it.sex, it.latitude, it.longitude)
                                                it.avatarMarker = addAvatarMarkers(avatar, it.latitude, it.longitude)
                                            }
                                        }
                                    } finally {
//                                        latcha.countDown()
                                    }
                                })
//                                latcha.wait()
                            } catch (e: Exception) {
                                e(e.message!!)
                            }
                        }

                        when (showType) {
                            1 -> {
                                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(myLatlng, zoom, 0f, 0f)), 100, null)
                            }
                            2 -> {
                                updateBounds(latlngs)
                            }
                        }
                    } catch (e: Exception) {
                        e(e.message!!)
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    lateinit var timer: Timer
    private fun startTimer() {
        try {
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    moveMarks()
                }
            }, 5000, 5000)
        } catch (e: Exception) {
            e(e.message!!)
        }
    }

    //endregion
    private lateinit var aMap: AMap
    private lateinit var mUiSettings: UiSettings
    private lateinit var centerPoint: Point
    private lateinit var locationMarker: Marker
    private var zoom = 17f
    private lateinit var locationClient: AMapLocationClient
    private lateinit var accuracyCircle: Circle
    private lateinit var searchCircle: Circle
    private lateinit var mShareSearch: ShareSearch

    private fun addLocationMarkers(title: String, sex: Int, latitude: Double, longitude: Double): Marker {
//        northMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.north))).position(center));
        var locationMarker = aMap.addMarker(
            MarkerOptions().anchor(0.5f, 1.0f)
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, if (sex == 1) R.drawable.point_a else R.drawable.point_b)))
                .title(title)
                .snippet("")
                .position(LatLng(latitude, longitude))
        )
//        locationMarker.showInfoWindow()
//        if (isCenterCamera) {
//            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(LatLng(latitude, longitude), zoom, 0f, 0f)), 100, null)
//        }
        return locationMarker
//        accuracyCircle = aMap.addCircle(
//            CircleOptions()
//                .center(LatLng(latitude,longitude))
//                .radius(0.0)
//                .fillColor(resources.getColor(R.color.location_accuracy))
//                .strokeWidth(0f)
//        )
    }

    private fun addAvatarMarkers(profileImg: Bitmap, latitude: Double, longitude: Double): Marker {
        val avatarImg = BitmapDescriptorFactory.fromBitmap(ImageUtils.toRoundBitmap(BitmapDescriptorFactory.fromBitmap(profileImg).bitmap))
        var avatarMarker = aMap.addMarker(MarkerOptions().anchor(0.5f, 1.2f).icon(avatarImg).position(LatLng(latitude, longitude)))
        return avatarMarker
    }

    private fun moveMarker(marker: Marker, latitude: Double, longitude: Double) {
        marker.position = LatLng(latitude, longitude)
//        marker.title = title
//        marker.snippet = snippet
    }

    private fun initMap() {
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         */
        Log.e("wangsc", MapsInitializer.sdcardDir)
        aMap = textureMapView.getMap()
        mUiSettings = aMap.getUiSettings()
        mUiSettings.setScrollGesturesEnabled(true) // 设置地图是否可以手势滑动
        mUiSettings.setZoomGesturesEnabled(true) // 设置地图是否可以手势缩放大小
        mUiSettings.setTiltGesturesEnabled(false) // 设置地图是否可以倾斜
        mUiSettings.setRotateGesturesEnabled(false) // 设置地图是否可以旋转
        mUiSettings.setZoomControlsEnabled(false) // 设置地图是否显示手势缩放
        mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM) // 设置缩放按钮位置
        mUiSettings.setMyLocationButtonEnabled(false) // 设置地图默认的定位按钮是否显示
        mUiSettings.setScaleControlsEnabled(true) // 比例尺
        aMap.setMyLocationEnabled(false) // 是否可触发定位并显示定位层
        aMap.setLoadOfflineData(true)
        locationClient = AMapLocationClient(this)
        aMap.setOnMapTouchListener {
            showType = 0
//            iv_showAll.setImageResource(R.drawable.show_all_off)
            tv_showAll.setTextColor(Color.BLACK)
            users?.forEach {
                it.view.findViewById<TextView>(R.id.tv_time).setTextColor(Color.BLACK)
            }

//            Thread.sleep(10000)
//            showType=2
//            iv_nag.setImageResource(R.drawable.navigation_off)
//            iv_showAll.setImageResource(R.drawable.show_all_on)
        }
//        aMap.setOnMapLongClickListener {
//            showType=0
//            moveMarks()
//        }
    }

    private fun updateBounds(latlngs: MutableList<LatLng>) {
        /*  val latLngs: MutableList<LatLng> = ArrayList()

            latLngs.add(LatLng(36.5333737316, 117.5162532850))
            latLngs.add(LatLng(36.5333737316, 117.5162532850))
            latLngs.add(LatLng(36.0015213728, 117.2966122508))
            latLngs.add(LatLng(36.0015213728, 117.2966122508))
            aMap.addPolyline(
                PolylineOptions()
                    .addAll(latLngs)
                    .width(55f)
                    .zIndex(1f)
                    .setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.bg_route_gray))
                    .color(Color.argb(255, 1, 1, 1))
            )
    //上面这一步已完成画线
    //上面这一步已完成画线
            aMap.addMarker(
                MarkerOptions()
                    .position(LatLng(36.5333737316, 117.5162532850))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_address_start_blue))
            )

            aMap.addMarker(
                MarkerOptions()
                    .position(LatLng(36.0015213728, 117.2966122508))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_address_end_orange))
            )
    //上面这一步则加上起始点的icon
    //上面这一步则加上起始点的icon*/
        val builder = LatLngBounds.Builder()
        latlngs.forEach {
            builder.include(it)
        }
        val bounds = builder.build()

//        LatLngBounds.Builder().include(LatLng(1.0,1.0))
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
        aMap.animateCamera(cameraUpdate, 100L, null as CancelableCallback?)
    }
}