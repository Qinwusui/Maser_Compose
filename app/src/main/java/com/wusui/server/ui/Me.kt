package com.wusui.server.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.blankj.utilcode.util.ToastUtils
import com.wusui.server.activity.createAlerter
import com.wusui.server.model.MainViewModel
import com.wusui.server.theme.dxtitle
import com.wusui.server.theme.hei
import com.wusui.server.theme.hui
import com.wusui.server.theme.yimahong

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Me(viewModel: MainViewModel) {

    val context = LocalContext.current
    val loginData = viewModel.loginData.collectAsState().value
    val isIgnoreBattery = viewModel.isIgnoreBattery.collectAsState().value
    val currentData by rememberUpdatedState(newValue = loginData)
    DisposableEffect(key1 = currentData, effect = {
        if (currentData.msg != "") {
            createAlerter(context) {
                setTitle(currentData.msg)
            }
        }
        onDispose { }
    })
    var loginDialogShow by remember {
        mutableStateOf(false)
    }
    var uName by remember {
        mutableStateOf("")
    }
    var pwd by remember {
        mutableStateOf("")
    }
    var showLogoutDialog by remember {
        mutableStateOf(false)
    }
    var showWhatsDialog by remember {

        mutableStateOf(false)
    }
    var showHowtoUseDialog by remember {
        mutableStateOf(false)
    }
    AnimatedVisibility(visible = showHowtoUseDialog) {
        AlertDialog(onDismissRequest = {
            showHowtoUseDialog = false
        }, title = {

            val qqText = buildAnnotatedString {
                pushStringAnnotation(
                    tag = "qq",
                    annotation = "https://qm.qq.com/cgi-bin/qm/qr?k=1paAk7fObeVKQLs5uWKWsT5HPlrMxlW7&" +
                            "jump_from=webapi&authKey=FNwmhxj9yS2FJqO/3TQ2G8L6k1FOnpY6F4Ei/+0zRM2gSKPV0CDru3Jgkj6rv78l"
                )
                withStyle(SpanStyle(
                    fontSize = 15.sp,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )){
                    append("QQ群号：329972361")
                }
                pop()
            }
            Column {
                Text(text = "使用说明")
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = """
                1.这是一款远程虚拟局域网联机程序，用于提供处于不同IP下的设备进行远程组网
                2.程序使用Jetpack Compose 开发，有bug及时反馈
                3.使用说明
                - 使用工具联机前，请在（我的）页登录，登录所需验证码可以在指定群聊查看
                - 登录完成后，选择（服务器）页，选择一个服务器，点击连接，稍等一会就会弹出连接成功对话框，并且可以复制IP地址
                - 与小伙伴使用该工具，连接到同一服务器，且开游戏房间的一方提供IP地址，另一方在游戏中输入IP地址即可联机
                - 若无法连接，请检查网络环境，并交替切换房主重试。
                - 开发者建议您忽略对Maser的电池优化功能，以保证其在后台存活能力，防止游戏短线。在（我的）页里有直达开关
                - 祝您游戏愉快
            """.trimIndent()
                )
                Spacer(modifier = Modifier.height(5.dp))
                Divider(modifier = Modifier.height(1.dp))
                Spacer(modifier = Modifier.height(5.dp))

                ClickableText(text =qqText  , onClick ={
                    qqText.getStringAnnotations(
                        tag = "qq",
                        start = it,
                        end = it
                    ).firstOrNull()?.let {ann->
                        val intent=Intent(Intent.ACTION_VIEW)
                        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.data=Uri.parse(ann.item)
                        context.startActivity(intent)
                    }
                })
                Text(text = "By 琴五岁", modifier = Modifier.align(Alignment.End))

            }
        }, buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(onClick = {
                    showHowtoUseDialog = false
                }) {
                    Text(text = "确认", color = yimahong)
                }
            }
        })
    }
    AnimatedVisibility(visible = showWhatsDialog) {
        AlertDialog(onDismissRequest = {
            showWhatsDialog = false
        }, title = {
            SelectionContainer {
                Column {
                    DisableSelection {
                        Text(
                            """
                            1.程序虽然不需要实名制，但是采用了与QQ号进行绑定的功能，防止程序滥用
                            2.验证码功能仅生效于该应用
                            3.需要验证码请在群内回复：我要注册验证码
                            4.支持获取验证码的群有： """.trimIndent()
                        )
                    }
                    Text(text = "329972361")
                    Text(text = "870845840")
                    DisableSelection {
                        Text(text = "其他群概不生效 \n5.在群内获取验证码后，请在QQ邮箱中检查验证码邮件，邮件可能因为网络延迟到达。")
                    }
                }
            }

        }, buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=2064508450")
                    )
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        ToastUtils.showShort("启动QQ失败了呢")
                    }
                }) {
                    Text(text = "联系开发者", color = dxtitle)
                }
                TextButton(onClick = { showWhatsDialog = false }) {
                    Text(text = "取消")
                }
                TextButton(onClick = {
                    showWhatsDialog = false
                    viewModel.logout()
                }) {
                    Text(text = "确认", color = yimahong)
                }
            }
        })
    }
    AnimatedVisibility(visible = showLogoutDialog) {
        AlertDialog(onDismissRequest = {
            showLogoutDialog = false
        }, title = {
            Text("确认退出？")
        }, dismissButton = {
            TextButton(onClick = { showLogoutDialog = false }) {
                Text(text = "取消")
            }
        }, confirmButton = {
            TextButton(onClick = {
                showLogoutDialog = false
                viewModel.logout()
            }) {
                Text(text = "确认")
            }
        })
    }
    AnimatedVisibility(visible = loginDialogShow) {
        AlertDialog(
            onDismissRequest = { loginDialogShow = false },
            title = {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "请输入用户信息", modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        value = uName,
                        onValueChange = { uName = it },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = dxtitle,
                            backgroundColor = Color.Transparent,
                            cursorColor = dxtitle,
                            focusedBorderColor = dxtitle,
                            unfocusedLabelColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = dxtitle,
                            placeholderColor = dxtitle
                        ),
                        label = {
                            Text(text = "请输入QQ号")
                        })
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        value = pwd,
                        label = {
                            Text(text = "请输入验证码")
                        },
                        onValueChange = { pwd = it },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = dxtitle,
                            backgroundColor = Color.Transparent,
                            cursorColor = dxtitle,
                            focusedBorderColor = dxtitle,
                            unfocusedLabelColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = dxtitle,
                            placeholderColor = dxtitle
                        )
                    )
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        showWhatsDialog = true
                        loginDialogShow = false
                    }) {
                        Text(text = "什么验证码？", color = yimahong)
                    }
                    TextButton(onClick = { loginDialogShow = false }) {
                        Text(text = "取消", color = hei)
                    }
                    TextButton(onClick = {
                        if (uName.isEmpty() || pwd.isEmpty()) {
                            createAlerter(context) {
                                setTitle("用户信息不完全！")
                            }
                        } else {
                            viewModel.login(uName, pwd)
                            loginDialogShow = false
                        }
                    }) {
                        Text(text = "登录", color = dxtitle)
                    }
                }
            },
        )
    }
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState, modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (viewModel.isLogin) {
                    Image(
                        painter = rememberAsyncImagePainter(model = "http://q2.qlogo.cn/headimg_dl?dst_uin=${viewModel.getUname()}&spec=100"),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Transparent, RoundedCornerShape(50.dp))
                            .combinedClickable(
                                onLongClick = {
                                    showLogoutDialog = true
                                },
                                onClick = {},
                                onDoubleClick = {}
                            )
                    )
                } else {
                    IconButton(onClick = { loginDialogShow = true }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            tint = dxtitle,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(text = if (viewModel.isLogin) viewModel.getUname() else "点击头像登录")
                    Text(
                        text = "程序仅供交流学习使用，禁止二改，商用，贩卖！",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = hui)
            )
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        viewModel.setBattery()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "忽略电池优化", fontSize = 25.sp)
                    Text(
                        text = "状态：${
                            if (isIgnoreBattery) "开启" else "关闭"
                        }"
                    )
                }
                Switch(checked = isIgnoreBattery, onCheckedChange = {
                    viewModel.setBattery()
                })
            }
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        showHowtoUseDialog = true
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = "使用说明", fontSize = 25.sp)

            }
        }
    }
}