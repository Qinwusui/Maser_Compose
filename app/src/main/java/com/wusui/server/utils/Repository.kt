package com.wusui.server.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
import androidx.core.content.edit
import com.wusui.server.MaserApp
import com.wusui.server.data.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random


object Repository {

//    private var configData = ConfigData()
    private const val URL_BASE=""
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
    }

//    private fun String.toMd5(): String {
//        try {
//            //获取md5加密对象
//            val instance: MessageDigest = MessageDigest.getInstance("MD5")
//            //对字符串加密，返回字节数组
//            val digest: ByteArray = instance.digest(this.toByteArray())
//            val sb: StringBuffer = StringBuffer()
//            for (b in digest) {
//                //获取低八位有效值
//                val i: Int = b.toInt() and 0xff
//                //将整数转化为16进制
//                var hexString = Integer.toHexString(i)
//                if (hexString.length < 2) {
//                    //如果是一位的话，补0
//                    hexString = "0$hexString"
//                }
//                sb.append(hexString)
//            }
//            return sb.toString()
//
//        } catch (e: NoSuchAlgorithmException) {
//            e.printStackTrace()
//        }
//
//        return ""
//    }

//    private suspend fun requestUrlAndPort() {
//        val c = HttpClient(Android) {
//            install(ContentNegotiation) {
//                gson {
//                    setPrettyPrinting()
//                }
//
//            }
//        }
//        val timeStamp = System.currentTimeMillis()
//        val strBuilder = StringBuilder()
//        val random = Random(System.currentTimeMillis() % 3)
//
//        for (i in 0 until 15) {
//            strBuilder.append(random.nextInt('a'.code, 'z'.code).toChar().toString())
//        }
//        val url = "http://api.liusui.xyz"
//        val port = "/1/classes/Url/iPBe000H"
//        val domain = "/1/classes/Url/vhka888I"
//        val portBody = c.get("$url$port") {
//
//            headers {
//                append("X-Bmob-SDK-Type", "API")
//                append("X-Bmob-Safe-Sign", "$port${timeStamp}520000$strBuilder".toMd5())
//                append("X-Bmob-Safe-Timestamp", timeStamp.toString())
//                append("X-Bmob-Noncestr-Key", strBuilder.toString())
//                append("X-Bmob-Secret-Key", "6b760c18ffb7e8c5")
//                append(HttpHeaders.ContentType, "application/json")
//            }
//        }
//        val domainBody = c.get("$url$domain") {
//            headers {
//                append("X-Bmob-SDK-Type", "API")
//                append("X-Bmob-Safe-Sign", "$domain${timeStamp}520000$strBuilder".toMd5())
//                append("X-Bmob-Safe-Timestamp", timeStamp.toString())
//                append("X-Bmob-Secret-Key", "6b760c18ffb7e8c5")
//                append("X-Bmob-Noncestr-Key", strBuilder.toString())
//
//                append(HttpHeaders.ContentType, "application/json")
//            }
//        }
//        val portData = portBody.body<PortData>()
//        val domainData = domainBody.body<PortData>()
//        configData = ConfigData(portData.urlName, domainData.urlName)
//    }


    suspend fun getConfig() = flowByIO {
//        requestUrlAndPort()
        val c =
            client.get("${URL_BASE}/Maser/configs?token=${"Qinsansui233...".encodeBase64()}")
        val config = c.body<Config>()
        config
    }

//    private fun transConfig(config: Config): Config {
//
//        val strings = config.config.split("\n")
//        val strBuilder = StringBuilder()
//        for (i in strings) {
//
//            if (i.startsWith("remote")) {
//                strBuilder.append("remote server.natappfree.cc ${configData.port}\n")
//            } else {
//                strBuilder.append(i + "\n")
//            }
//        }
//        return config.copy(
//            config = strBuilder.toString(),
//            pass = config.pass,
//            server = config.server,
//            users = config.users
//        )
//
//    }

    suspend fun getBannerData() = flowByIO {
        val c = client.get("${URL_BASE}/Maser/banner")
        val bannerData = c.body<BannerData>()
        bannerData
    }


    //存储SP
    fun spPermissionSave(v: Boolean) {
        val sp = MaserApp.context.getSharedPreferences("showPermissionDialog", Context.MODE_PRIVATE)
        sp.edit(true) {
            this.putBoolean("isShow", v)
        }
    }

    fun spPermissionGet(): Boolean {
        val sp = MaserApp.context.getSharedPreferences("showPermissionDialog", Context.MODE_PRIVATE)
        return sp.getBoolean("isShow", false)
    }

    //注册功能写在机器人里
//    fun register(userName: String, pwd: String) = flowByIO {
//        val regData = client.post("${configData.domain}/UserInfo/add") {
//            setBody(
//                UserInfo(
//                    qq = userName,
//                    code = pwd,
//                    token = "Qinsansui233...".encodeBase64()
//                )
//            )
//        }
//        regData.body<UserBody>()
//    }
    private suspend fun autoLogin(): UserBody {
        val session = readData().decodeBase64String()
        val t = System.currentTimeMillis()
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val loginData = client.post("${URL_BASE}/UserInfo/login") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(LoginBody((session + date.format(t)).encodeBase64()))
        }
        val body = loginData.body<UserBody>()
        if (body.code == 0 && body.msg != "") {
            saveData(session)
        }
        return loginData.body()
    }

    fun logout() {
        val sp = MaserApp.context.getSharedPreferences("u", Context.MODE_PRIVATE)
        sp.edit(true) {
            clear()
        }

    }

    @SuppressLint("BatteryLife")
    fun setIgnoreBattery(context: Context = MaserApp.context) = flowByIO {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            val intent = Intent(
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            intent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(intent)
        } else {
            val powerUsageIntent = Intent(ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            powerUsageIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
            val resolveInfo = context.packageManager.resolveActivity(powerUsageIntent, 0)
            //判断系统是否有这个页面
            if (resolveInfo != null) {
                context.startActivity(powerUsageIntent)
            }
        }
        powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun getIgnoreBattery(context: Context = MaserApp.context) = flowByIO {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        powerManager.isIgnoringBatteryOptimizations(context.packageName)

    }

    suspend fun login(userName: String = "", pwd: String = "") = flowByIO {
        val sp = MaserApp.context.getSharedPreferences("u", Context.MODE_PRIVATE)
        val session = sp.getString("session", "")!!
        if (userName.isEmpty() && pwd.isEmpty() && session.isNotEmpty()) {
            return@flowByIO autoLogin()
        }
        val t = System.currentTimeMillis()
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val loginBody = "$userName|~|$pwd|~|"
        val loginData = client.post("${URL_BASE}/UserInfo/login") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(LoginBody((loginBody + date.format(t)).encodeBase64()))
        }
        val body = loginData.body<UserBody>()
        if (body.code == 0 && body.msg != "") {
            saveData(loginBody)
        }
        loginData.body()
    }

    //读取用户数据
    private fun readData(): String {
        val sp = MaserApp.context.getSharedPreferences("u", Context.MODE_PRIVATE)
        return sp.getString("session", "")!!
    }

    fun readUname(): String {
        val sp = MaserApp.context.getSharedPreferences("u", Context.MODE_PRIVATE)
        return sp.getString("session", "")!!.decodeBase64String().split("|~|")[0]
    }

    //保存用户数据
    private fun saveData(loginBody: String) {
        val sp = MaserApp.context.getSharedPreferences("u", Context.MODE_PRIVATE)
        sp.edit(true) {
            putString("session", loginBody.encodeBase64())
        }
    }
}

private fun <T> flowByIO(context: CoroutineContext = Dispatchers.IO, block: suspend () -> T) =
    flow {
        val result = block()
        emit(result)
    }.distinctUntilChanged().flowOn(context)
