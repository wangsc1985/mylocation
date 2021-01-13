package com.wangsc.mylocation.models

class LocationState private constructor(val state: Boolean) {
    companion object {
        fun getInstance(state: Boolean): LocationState {
            return LocationState(state)
        }
    }
}