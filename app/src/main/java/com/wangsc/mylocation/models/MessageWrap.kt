package com.wangsc.mylocation.models

class MessageWrap private constructor(val message: String) {
    companion object {
        fun getInstance(message: String): MessageWrap {
            return MessageWrap(message)
        }
    }
}