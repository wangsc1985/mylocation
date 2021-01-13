package com.wangsc.mylocation.models

import android.graphics.Bitmap
import android.media.Image
import android.view.View
import com.amap.api.maps.model.Circle
import com.amap.api.maps.model.Marker

class User {
    var name:String
    var nick:String
    var sex:Int
    var avatar:String
    var address:String
    var phone:String
    var latitude:Double
    var longitude:Double
    var locationTime: DateTime
    var accuracy:Float //精    度
    var speed:Float //速    度
    var bearing:Float //角    度
    var teamName:String
    lateinit var avatarImg:Bitmap
    lateinit var locationMarker: Marker
    lateinit var avatarMarker:Marker
    lateinit var accuracyCircle:Circle
    lateinit var view: View

    constructor(name: String, nick: String, sex: Int, avatar: String, address: String, phone: String, latitude: Double, longitude: Double, locationTime: DateTime,accuracy:Float,speed:Float,bearing:Float,teamName: String){
        this.name = name
        this.nick = nick
        this.sex = sex
        this.avatar = avatar
        this.address = address
        this.phone = phone
        this.latitude = latitude
        this.longitude = longitude
        this.locationTime = locationTime
        this.accuracy = accuracy
        this.speed = speed
        this.bearing = bearing
        this.teamName = teamName
    }
}