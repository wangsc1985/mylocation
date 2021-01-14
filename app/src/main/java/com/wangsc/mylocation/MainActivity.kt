package com.wangsc.mylocation

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.amap.api.maps.*
import com.amap.api.maps.AMap.CancelableCallback
import com.amap.api.maps.model.*
import com.amap.api.maps.model.LatLngBounds
import com.wangsc.mylocation.callbacks.CloudCallback
import com.wangsc.mylocation.models.*
import com.wangsc.mylocation.sevice.LocationService
import com.wangsc.mylocation.utils.ImageUtils
import com.wangsc.mylocation.utils.LoadFileUtils
import com.wangsc.mylocation.utils._CloudUtils
import com.wangsc.mylocation.utils._Utils
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.CyclicBarrier
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val MY_PERMISSIONS_REQUEST: Int = 100
    private var locationIsOn = false
    private var localUserList: MutableList<User>? = null
    private var showType = 2
    private var selectedUserName = ""
    private var currentTeamName = ""

    //region 动态权限申请
    var permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
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

    val checkedBoxColor = R.color.checked_box
    /**
     * 队列按钮被选择
     */
    private fun teamMode() {
        layout_showAll.setBackgroundResource(checkedBoxColor)
        iv_showAll.setImageResource(R.drawable.people_checked)
        localUserList?.forEach {
            it.view?.findViewById<LinearLayout>(R.id.layout_root)?.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    /**
     * 用户被选择
     */
    private fun userMode() {
        layout_showAll.setBackgroundColor(Color.TRANSPARENT)
        iv_showAll.setImageResource(R.drawable.people_unchecked)
        localUserList?.forEach {
            if (it.name == selectedUserName) {
                it.view?.findViewById<LinearLayout>(R.id.layout_root)?.setBackgroundResource(checkedBoxColor)
            } else {
                it.view?.findViewById<LinearLayout>(R.id.layout_root)?.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    /**
     * 自由模式，用户和队列都不选
     */
    private fun freeMode() {
        layout_showAll.setBackgroundColor(Color.TRANSPARENT)
        localUserList?.forEach {
            it.view?.findViewById<LinearLayout>(R.id.layout_root)?.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    /**
     * 分享按钮停止
     */
    private fun shareButtonOff() {
        layout_share.setBackgroundColor(Color.TRANSPARENT)
        iv_share.setImageResource(R.drawable.share_off)
    }

    /**
     * 分享按钮开启
     */
    private fun shareButtonOn() {
        layout_share.setBackgroundResource(checkedBoxColor)
        iv_share.setImageResource(R.drawable.share_on)
    }

    //    lateinit var loadingDialog:ProgressDialog
    private fun showLoadingDialog() {
//                loadingDialog = ProgressDialog(this)
//                loadingDialog.setMessage("正在加载 . . . ")
//                loadingDialog.setCancelable(false)
//                loadingDialog.setCanceledOnTouchOutside(false)
//                loadingDialog.show()
    }

    private fun hideLoadingDialog() {
//        loadingDialog.dismiss()
    }

    /**
     * 添加用户按钮
     */
    private fun addUserView(user: User) {
        val view = View.inflate(this, R.layout.inflate_location_user, null)
        val avatarView = view.findViewById<ImageView>(R.id.iv_avatar)
        val timeView = view.findViewById<TextView>(R.id.tv_time)

        avatarView.setOnClickListener {
            selectedUserName = user.name
            showType = 1
            moveUserLocation()
            userMode()
        }

        avatarView.setImageBitmap(user.avatarImg)
        val span = (System.currentTimeMillis() - user.locationTime.timeInMillis) / 1000
        runOnUiThread {
            tv_team.setText(user.teamName)
            currentTeamName = user.teamName
            timeView.setText(span2time(span))
            layout_users.addView(view)
        }
        user.view = view
    }

    /**
     * 刷新用户按钮
     */
    private fun updateUserView(user: User) {
        user.view?.let {view->
            val timeView = view.findViewById<TextView>(R.id.tv_time)
            currentTeamName = user.teamName
            runOnUiThread {
                tv_team.setText(user.teamName)
                val span = (System.currentTimeMillis() - user.locationTime.timeInMillis) / 1000
                if (showType == 1 && user.name == selectedUserName && span > 20) {
                    _Utils.playSound(this)
                }
                timeView.setText(span2time(span))
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGetMessage(message: LocationMessage) {
        if (message.accuracy > 30 || message.delay > 20) {
            tv_info.visibility = View.VISIBLE
            tv_info.setText(message.message)
        } else {
            tv_info.visibility = View.INVISIBLE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGetMessage(state: LocationState) {
        if (!state.state) {
            tv_info.visibility = View.INVISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textureMapView.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)

        showLoadingDialog()
//        val sha1 = Abc.sHA1(this)
//        e(sha1)
//        _Utils.log2file("run", "SHA1", sha1)

        /**
         * 如果全部权限授权通过，直接运行初始化方法。
         * 如果有权限未授权，去申请权限，根据授权结果，决定是否进行初始化方法
         */
        if (requestPermission() == true) {
            initOnCreate()
        }
    }

    override fun onResume() {
        startTimer()
        super.onResume()
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
            Thread({
                initMarks()
            }).start()

            teamMode()

            if (_Utils.isRunService(this, LocationService::class.qualifiedName!!)) {
                shareButtonOn()
                locationIsOn = true
            }
            layout_share.setOnClickListener {
                startShareLocation(true)
            }
            layout_share.setOnLongClickListener {
                AlertDialog.Builder(this).setMessage("本次开启位置分享后不会自动关闭，请记得手动关闭。").setNegativeButton("继续", { dialog, which ->
                    startShareLocation(false)
                }).setPositiveButton("退出", null).show()
                true
            }

            layout_showAll.setOnClickListener {
                showType = 2
                moveUserLocation()
                teamMode()
            }
            layout_showAll.setOnLongClickListener {
                if (dc.getSetting(Setting.KEYS.phone) == null) {
                    loginUserDialog()
                } else {
                    AlertDialog.Builder(this).setItems(arrayOf("切换用户", "队伍更名")) { dialog, which ->
                        when (which) {
                            0 -> {
                                loginUserDialog()
                            }
                            1 -> {
                                teamRename()
                            }
                        }
                    }.show()
                }
                true
            }
        } catch (e: Exception) {
            e("initView "+e.message!!)
        }

    }

    private fun teamRename() {
        val dc = DataContext(this)
        val set = dc.getSetting(Setting.KEYS.team_code)
        if (set != null) {
            val view = View.inflate(this, R.layout.inflate_dialog_text, null)
            val et = view.findViewById<EditText>(R.id.et_value)
            e("team name : $currentTeamName")
            et.setText(currentTeamName)
            AlertDialog.Builder(this).setTitle("队伍更名").setView(view).setNegativeButton("提交", DialogInterface.OnClickListener { dialog, which ->
                _CloudUtils.editTeam(this, set.string, et.text.toString(), null)
            }).show()
        }
    }

    private fun loginUserDialog() {
        val dc = DataContext(this)
        val view = View.inflate(this, R.layout.dialog_team, null)
        val phoneView = view.findViewById<EditText>(R.id.et_phone)
        val teamCodeView = view.findViewById<EditText>(R.id.et_teamCode)
        phoneView.setText(dc.getSetting(Setting.KEYS.phone)?.string)
        teamCodeView.setText(dc.getSetting(Setting.KEYS.team_code)?.string)
        AlertDialog.Builder(this).setTitle("用户信息").setView(view).setPositiveButton("提交")
        { dialog, which ->
            phone = phoneView.text.toString()
            teamCode = teamCodeView.text.toString()
            dc.editSetting(Setting.KEYS.phone, phone)
            dc.editSetting(Setting.KEYS.team_code, teamCode)
        }.show();
    }

    private fun startShareLocation(isAutoClose: Boolean) {
        if (!isNotifyAllowed()) {
            openNotifySetting()
        } else {
            val intent = Intent(this, LocationService::class.java)
            if (!locationIsOn) {
                intent.putExtra("isAutoClose", isAutoClose)
                startService(intent)
                shareButtonOn()
                locationIsOn = true
            } else {
                AlertDialog.Builder(this).setMessage("是否停止位置分享？").setPositiveButton("是", DialogInterface.OnClickListener { dialog, which ->
                    stopService(intent)
                    shareButtonOff()
                    locationIsOn = false
                }).setNegativeButton("否", null).show()
            }
        }
    }

    private fun moveUserLocation() {
        _CloudUtils.getLocations(this, teamCode, object : CloudCallback {
            override fun excute(code: Int, result: Any?) {
                hideLoadingDialog()
                if (code == 0) {
                    try {
                        var netUserList = result as MutableList<User>
                        var boundsLatlngs: MutableList<LatLng> = ArrayList()
                        var selectedUserLatlng: LatLng? = null
                        netUserList.forEach { netUser ->
                            if (selectedUserName.isEmpty()) {
                                if (netUser.phone == phone) {
                                    selectedUserName = netUser.name
                                    selectedUserLatlng = LatLng(netUser.latitude, netUser.longitude)
                                }
                            } else {
                                if (selectedUserName == netUser.name) {
                                    selectedUserLatlng = LatLng(netUser.latitude, netUser.longitude)
                                }
                            }
                            boundsLatlngs.add(LatLng(netUser.latitude, netUser.longitude))
                            /**
                             * 因为本地localUserList保存有用户的locationMarker，avatarMarker，accuracyCircle，所以不能将从网络获取的netUserList直接复制给localUserList，那样会覆盖上面三个值，
                             * 所以只能单个赋值
                             */
                            if (localUserList != null) {
                                var localUser = localUserList!!.firstOrNull {
                                    it.name==netUser.name
                                }
                                if (localUser != null) {
                                    localUser.setValus(netUser)
                                    if (localUser.locationMarker != null && localUser.avatarMarker != null && localUser.accuracyCircle != null) {
                                        updateUserView(localUser)
                                        moveMarker(localUser)
                                    }
                                }
                            }
                        }

                        when (showType) {
                            1 -> {
                                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(selectedUserLatlng, zoom, 0f, 0f)), 100, null)
                            }
                            2 -> {
                                updateBounds(boundsLatlngs)
                            }
                        }
                    } catch (e: Exception) {
                        e("moveUserLocation "+e.message)
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
        } else if (span > 20) {
            time1 = "${span}秒前"
        } else {
            time1 = "实时"
        }
        return time1
    }

    private fun initMarks() {
        _CloudUtils.getLocations(this, teamCode, object : CloudCallback {
            override fun excute(code: Int, result: Any?) {
                if (code == 0) {
                    try {
                        localUserList = result as MutableList<User>
                        e("user list size : ${localUserList!!.size}")
                        var boundsLatlngs: MutableList<LatLng> = ArrayList()
                        var selectedUserLatlng: LatLng? = null

                        localUserList?.forEach { localUser ->
                            try {
                                if (selectedUserName.isEmpty()) {
                                    if (localUser.phone == phone) {
                                        selectedUserName = localUser.name
                                        selectedUserLatlng = LatLng(localUser.latitude, localUser.longitude)
                                    }
                                } else {
                                    if (selectedUserName == localUser.name) {
                                        selectedUserLatlng = LatLng(localUser.latitude, localUser.longitude)
                                    }
                                }
                                boundsLatlngs.add(LatLng(localUser.latitude, localUser.longitude))

                                var avatarUrl = ""
                                val cb = CyclicBarrier(2)
                                _CloudUtils.getDownLoadPath(this@MainActivity, localUser.avatar, CloudCallback { code, result ->
                                    try {
                                        if (code == 0) {
                                            avatarUrl = result.toString()
                                            var url = LoadFileUtils.loadFileFromHttp(avatarUrl, "${localUser.name}.jpg")
                                            if (!url.isEmpty()) {
                                                localUser.avatarImg = BitmapFactory.decodeFile(url)
                                                addUserView(localUser)
                                                addMarkers(localUser)
                                            }
                                        }
                                    } finally {
                                        cb.await()
                                    }
                                })

                                cb.await()
                            } catch (e: Exception) {
                                e("initMarks "+e.message!!)
                            }
                        }

                        when (showType) {
                            1 -> {
                                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(selectedUserLatlng, zoom, 0f, 0f)), 100, null)
                            }
                            2 -> {
                                updateBounds(boundsLatlngs)
                            }
                        }
                    } catch (e: Exception) {
                        e("initMarks "+e.message!!)
                    }
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this);
    }

    lateinit var timer: Timer
    private fun startTimer() {
        try {
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (showType == 0) {
                        showType = preShowType
                        when (showType) {
                            // TODO: 2021/1/13
                            1 -> {
                                userMode()
                            }
                            2 -> {
                                teamMode()
                            }
                        }
                    }
                    moveUserLocation()
                }
            }, 10000, 10000)
        } catch (e: Exception) {
            e("startTimer "+e.message!!)
        }
    }

    //endregion
    private lateinit var aMap: AMap
    private lateinit var mUiSettings: UiSettings

    //    private lateinit var locationClient: AMapLocationClient
    private var zoom = 15f

    private fun addMarkers(user: User) {
        user.locationMarker = aMap.addMarker(
            MarkerOptions()
                .anchor(0.5f, 1.0f)
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, if (user.sex == 1) R.drawable.point_a else R.drawable.point_b)))
                .position(LatLng(user.latitude, user.longitude))
        )
        user.avatarMarker = aMap.addMarker(
            MarkerOptions()
                .anchor(0.5f, 1.16f)
                .icon(BitmapDescriptorFactory.fromBitmap(ImageUtils.toRoundBitmap(BitmapDescriptorFactory.fromBitmap(user.avatarImg).bitmap)))
                .position(LatLng(user.latitude, user.longitude))
        )
        user.accuracyCircle = aMap.addCircle(
            CircleOptions()
                .center(LatLng(user.latitude, user.longitude))
                .radius(0.0)
                .fillColor(resources.getColor(R.color.location_accuracy))
                .strokeWidth(0f)
        )
    }

    private fun moveMarker(user: User) {
        try {
            val latlng = LatLng(user.latitude, user.longitude)
            user.locationMarker?.position = latlng
            user.avatarMarker?.position = latlng
            user.accuracyCircle?.center = latlng
            user.accuracyCircle?.radius = user.accuracy.toDouble()
        } catch (e: Exception) {
            e("moveMarker : ${e.message}")
        }
    }

    private var preShowType = 0
    private fun initMap() {
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         */
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
//        locationClient = AMapLocationClient(this)
        aMap.setOnMapTouchListener {
            if (showType != 0) {
                preShowType = showType
            }
            timer.cancel()
            startTimer()
            showType = 0
            freeMode()
            zoom = aMap.cameraPosition.zoom
        }

    }

    private fun updateBounds(latlngs: MutableList<LatLng>) {
        val builder = LatLngBounds.Builder()
        latlngs.forEach {
            builder.include(it)
        }
        val bounds = builder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
        aMap.animateCamera(cameraUpdate, 100L, null as CancelableCallback?)
    }
}