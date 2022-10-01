package com.wusui.server.activity

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.mob.pushsdk.MobPush
import com.tapadoo.alerter.Alerter
import com.wusui.server.MaserApp
import com.wusui.server.model.MainViewModel
import com.wusui.server.push.ConfigPushReceiver
import com.wusui.server.theme.MaserTheme
import com.wusui.server.theme.colorAccent
import com.wusui.server.ui.MainScreen

class MainActivity : ComponentActivity() {
    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    private val configPushReceiver = ConfigPushReceiver()
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobPush.addPushReceiver(configPushReceiver)
        LocalBroadcastManager.getInstance(MaserApp.context)
            .registerReceiver(mainViewModel.r, IntentFilter("connectionState"))
        PermissionUtils.permission(Manifest.permission.INTERNET).request()
        if (!mainViewModel.getSp()) {
            AlertDialog.Builder(this)
                .setTitle("权限申请与隐私透明化")
                .setMessage(
                    """
                Maser需要以下权限才可正常使用
                1.访问网络，用于获取服务器配置
                2.建立VPN隧道，用于连接远程服务器
                3.电池优化，用于保持程序在后台的运行稳定性
                4.App使用Mob提供Push服务
                Maser不会将任何个人隐私通过任何方式发布到网络及其他任何地方，请知悉！
                尊重开发者劳动成果，请勿二次修改本程序，本程序完全免费，一切功能仅供学习交流使用
                    """.trimIndent()
                )
                .setPositiveButton(
                    "同意"
                ) { dialog, _ ->
                    checkVpn()
                    mainViewModel.getBattery()
                    mainViewModel.saveSp(true)

                    dialog.dismiss()
                }
                .setNegativeButton("不同意") { d, _ ->
                    ToastUtils.showShort("那么，您将无法正常使用该程序")
                    d.dismiss()
                }.show()
        }

        setContent {
            MaserTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MainScreen(mainViewModel)
                }
            }
        }
    }

    private fun checkVpn() {

        val intent = VpnService.prepare(this.applicationContext)
        if (intent != null) {
            result.launch(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityUtils.finishAllActivities()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getBattery()
    }
}

fun createAlerter(context: Context, block:Alerter.() -> Unit) =
    Alerter.create(ActivityUtils.getActivityByContext(context)!!)
        .setBackgroundColorInt(colorAccent.toArgb())
        .apply { block() }
        .show()
