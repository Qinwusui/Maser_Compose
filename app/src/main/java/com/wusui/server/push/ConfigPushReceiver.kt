package com.wusui.server.push

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PathUtils
import com.mob.pushsdk.MobPushCustomMessage
import com.mob.pushsdk.MobPushNotifyMessage
import com.mob.pushsdk.MobPushReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class ConfigPushReceiver : MobPushReceiver {
    override fun onCustomMessageReceive(p0: Context?, p1: MobPushCustomMessage?) {

    }

    override fun onNotifyMessageReceive(p0: Context?, p1: MobPushNotifyMessage) {

    }

    override fun onNotifyMessageOpenedReceive(p0: Context?, p1: MobPushNotifyMessage) {

    }

    override fun onTagsCallback(
        p0: Context?,
        p1: Array<out String>?,
        p2: Int,
        p3: Int
    ) {
    }

    override fun onAliasCallback(p0: Context?, p1: String?, p2: Int, p3: Int) {
    }
}