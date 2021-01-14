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
    var avatarImg:Bitmap?=null
    var locationMarker: Marker?=null
    var avatarMarker:Marker?=null
    var accuracyCircle:Circle?=null
    var view: View?=null

    /**
     * 设置除avatarImg，locationMarker，avatarMarker，accuracyCircle，view之外的所有属性值
     */
    fun setValus(user:User){
        this.name = user.name
        this.nick = user.nick
        this.sex = user.sex
        this.avatar = user.avatar
        this.address = user.address
        this.phone = user.phone
        this.latitude = user.latitude
        this.longitude = user.longitude
        this.locationTime = user.locationTime
        this.accuracy = user.accuracy
        this.speed = user.speed
        this.bearing = user.bearing
        this.teamName = user.teamName
    }

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