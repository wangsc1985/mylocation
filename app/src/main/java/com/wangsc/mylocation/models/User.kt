package com.wangsc.mylocation.models

import android.view.View
import com.amap.api.maps.model.Marker
import com.wangsc.mylocation.models.DateTime

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
    var teamName:String
    lateinit var locationMarker: Marker
    lateinit var avatarMarker:Marker
    lateinit var view: View

    constructor(name: String,nick:String,sex:Int,avatar:String,address:String,phone:String,latitude:Double,longitude:Double,locationTime: DateTime,teamName:String){
        this.name = name
        this.nick = nick
        this.sex = sex
        this.avatar = avatar
        this.address = address
        this.phone = phone
        this.latitude = latitude
        this.longitude = longitude
        this.locationTime = locationTime
        this.teamName = teamName
    }
}