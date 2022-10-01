package com.wusui.server.model

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.VibrateUtils
import com.wusui.server.MaserApp
import com.wusui.server.data.BannerData
import com.wusui.server.data.Config
import com.wusui.server.data.UserBody
import com.wusui.server.theme.colorPrimary
import com.wusui.server.utils.Repository
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@SuppressLint("HardwareIds", "ServiceCast")
class MainViewModel : ViewModel() {
    var isLogin by mutableStateOf(false)
        private set
    var mainColor by mutableStateOf(colorPrimary)
    var isConnectedDialogShow by mutableStateOf(false)
    var isDisConnectDialogShow by mutableStateOf(false)
    var itemClick by mutableStateOf(true)
    val r = object : BroadcastReceiver() {
        override fun onReceive(c: Context, t: Intent) {
            setState(t.getStringExtra("state"))
            when (t.getStringExtra("state")) {
                "CONNECTED" -> {
                    isConnected = true
                    isConnectedDialogShow = true
                }
                "tryDis" -> {
                    isDisConnectDialogShow = true
                }
            }
//            isConnected = t.getStringExtra("state") ==
//            isConnectedDialogShow = t.getStringExtra("state") == "CONNECTED"
//            isDisConnectDialogShow = t.getStringExtra("state") == "tryDis"
        }
    }

    var isConnected by mutableStateOf(false)
        private set

    fun disconnect() {
        isConnected = false
        OpenVPNThread.stop()
        isDisConnectDialogShow = false

        VibrateUtils.vibrate(200)
    }

    fun connect(config: Config, index: Int) {
        val co = _config.value.config
        val u = config.users[index]
        val p = config.pass[index]

        try {
            OpenVpnApi.startVpn(MaserApp.context, co, "wusui", u, p)
        } catch (e: Exception) {
            LogUtils.e(e)
        }
    }


    var stateString by mutableStateOf("")

    private fun setState(s: String?) {
        stateString = s.toString()
    }

    var title by mutableStateOf("Maser")


    init {
        getConfig()
    }

    fun saveSp(v: Boolean) = Repository.spPermissionSave(v)
    fun getSp() = Repository.spPermissionGet()

    //配置文件
    private val _config = MutableStateFlow(Config())
    val config = _config.asStateFlow()

    //BannerData
    private val _bannerData = MutableStateFlow(BannerData())
    val bannerData = _bannerData.asStateFlow()
    fun getConfig() {
        viewModelScope.launch {
            Repository.getConfig().collect {
                _config.value = it
            }
            Repository.getBannerData().collect {
                _bannerData.value = it
            }
            Repository.login().collect {
                _loginData.value = it
            }
            isLogin = _loginData.value.code == 0
            getBattery()
        }
    }

    //    private val _regData = MutableStateFlow(RegData(mutableListOf()))
//    val regData = _regData.asStateFlow()
//    private val _err = MutableStateFlow(ErrorData(0, ""))
//    val err = _err.asStateFlow()
//    fun register(uName: String, pwd: String) {
//        viewModelScope.launch {
//            Repository.register(uName, pwd).collect {
//                when (it) {
//                    is RegData -> {
//                        _regData.value = it
//                    }
//                    is ErrorData -> _err.value = it
//                }
//            }
//        }
//    }
    fun getUname() = Repository.readUname()
    private val _loginData = MutableStateFlow(UserBody(-2, ""))
    val loginData = _loginData.asStateFlow()
    fun login(userName: String, pwd: String) {
        viewModelScope.launch {
            Repository.login(userName, pwd).collect {
                _loginData.value = it
                isLogin = it.code == 0
            }
        }
    }

    private val _isIgnoreBattery = MutableStateFlow(false)
    val isIgnoreBattery = _isIgnoreBattery.asStateFlow()
    fun setBattery() {
        viewModelScope.launch {
            Repository.setIgnoreBattery().collect {
                _isIgnoreBattery.value = it
            }
        }
    }

    fun getBattery() {
        viewModelScope.launch {
            Repository.getIgnoreBattery().collect {
                _isIgnoreBattery.value = it
            }
        }
    }

    fun logout() {
        _loginData.value = _loginData.value.copy(-2, "")
        isLogin = false
        Repository.logout()
    }
}