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
                    append("QQ?????????329972361")
                }
                pop()
            }
            Column {
                Text(text = "????????????")
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = """
                1.????????????????????????????????????????????????????????????????????????IP??????????????????????????????
                2.????????????Jetpack Compose ????????????bug????????????
                3.????????????
                - ??????????????????????????????????????????????????????????????????????????????????????????????????????
                - ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????IP??????
                - ??????????????????????????????????????????????????????????????????????????????????????????IP????????????????????????????????????IP??????????????????
                - ????????????????????????????????????????????????????????????????????????
                - ???????????????????????????Maser?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                - ??????????????????
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
                Text(text = "By ?????????", modifier = Modifier.align(Alignment.End))

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
                    Text(text = "??????", color = yimahong)
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
                            1.???????????????????????????????????????????????????QQ?????????????????????????????????????????????
                            2.????????????????????????????????????
                            3.?????????????????????????????????????????????????????????
                            4.????????????????????????????????? """.trimIndent()
                        )
                    }
                    Text(text = "329972361")
                    Text(text = "870845840")
                    DisableSelection {
                        Text(text = "????????????????????? \n5.????????????????????????????????????QQ????????????????????????????????????????????????????????????????????????")
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
                        ToastUtils.showShort("??????QQ????????????")
                    }
                }) {
                    Text(text = "???????????????", color = dxtitle)
                }
                TextButton(onClick = { showWhatsDialog = false }) {
                    Text(text = "??????")
                }
                TextButton(onClick = {
                    showWhatsDialog = false
                    viewModel.logout()
                }) {
                    Text(text = "??????", color = yimahong)
                }
            }
        })
    }
    AnimatedVisibility(visible = showLogoutDialog) {
        AlertDialog(onDismissRequest = {
            showLogoutDialog = false
        }, title = {
            Text("???????????????")
        }, dismissButton = {
            TextButton(onClick = { showLogoutDialog = false }) {
                Text(text = "??????")
            }
        }, confirmButton = {
            TextButton(onClick = {
                showLogoutDialog = false
                viewModel.logout()
            }) {
                Text(text = "??????")
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
                    Text(text = "?????????????????????", modifier = Modifier.align(Alignment.Start))
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
                            Text(text = "?????????QQ???")
                        })
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        value = pwd,
                        label = {
                            Text(text = "??????????????????")
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
                        Text(text = "??????????????????", color = yimahong)
                    }
                    TextButton(onClick = { loginDialogShow = false }) {
                        Text(text = "??????", color = hei)
                    }
                    TextButton(onClick = {
                        if (uName.isEmpty() || pwd.isEmpty()) {
                            createAlerter(context) {
                                setTitle("????????????????????????")
                            }
                        } else {
                            viewModel.login(uName, pwd)
                            loginDialogShow = false
                        }
                    }) {
                        Text(text = "??????", color = dxtitle)
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
                    Text(text = if (viewModel.isLogin) viewModel.getUname() else "??????????????????")
                    Text(
                        text = "??????????????????????????????????????????????????????????????????",
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
                    Text(text = "??????????????????", fontSize = 25.sp)
                    Text(
                        text = "?????????${
                            if (isIgnoreBattery) "??????" else "??????"
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
                Text(text = "????????????", fontSize = 25.sp)

            }
        }
    }
}