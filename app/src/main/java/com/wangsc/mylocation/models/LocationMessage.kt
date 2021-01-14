package com.wangsc.mylocation.models

class LocationMessage private constructor(val delay:Long, val accuracy:Float, val message: String) {
    companion object {
        fun getInstance(delay:Long,accuracy: Float,message: String): LocationMessage {
            return LocationMessage(delay,accuracy,message)
        }
    }
}