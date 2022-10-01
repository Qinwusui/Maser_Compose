package com.wusui.server

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.mob.MobSDK
import com.mob.pushsdk.MobPush

class MaserApp:Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context=this
        MobSDK.init(this)
        MobSDK.submitPolicyGrantResult(true)
        MobPush.initMobPush()
    }
}