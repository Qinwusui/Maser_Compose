package com.wusui.server.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.VibrateUtils
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.wusui.server.activity.createAlerter
import com.wusui.server.model.MainViewModel
import com.wusui.server.theme.colorPrimary
import com.wusui.server.theme.dxtitle
import com.wusui.server.theme.yimahong
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.coroutines.delay

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Server(
    mainViewModel: MainViewModel
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val config by mainViewModel.config.collectAsState()
    val bannerData by mainViewModel.bannerData.collectAsState()

    val cor = rememberCoroutineScope()
    var showContinue by remember {
        mutableStateOf(false)
    }
    AnimatedVisibility(visible = showContinue) {
        AlertDialog(onDismissRequest = { showContinue = false },
            title = {
                Column {
                    Text(
                        text = """
                            --?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                            --?????????????????????????????????????????????????????????????????????~
                        """.trimIndent()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showContinue = false }) {
                    Text(text = "??????")

                }
            }
        )
    }
    AnimatedVisibility(visible = mainViewModel.isConnectedDialogShow) {
        mainViewModel.itemClick = true
        AlertDialog(
            onDismissRequest = { mainViewModel.isConnectedDialogShow = false },
            confirmButton = {
                Row {
                    TextButton(onClick = {
                        ClipboardUtils.copyText(VpnStatus.getLastCleanLogMessage(context))
                        VibrateUtils.vibrate(2000)
                        createAlerter(context) {
                            setTitle("IP?????????????????????")
                        }
                        mainViewModel.isConnectedDialogShow = false
                    }) {
                        Text(text = "??????IP", color = colorPrimary)
                    }
                    TextButton(onClick = { showContinue = true }) {
                        Text(text = "????????????")
                    }
                }


            },
            title = {
                Column {
                    Text(
                        text = "????????????\n????????????IP??????${VpnStatus.getLastCleanLogMessage(context)}",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

            },


            )
    }
    AnimatedVisibility(visible = mainViewModel.isDisConnectDialogShow) {
        AlertDialog(onDismissRequest = { mainViewModel.isDisConnectDialogShow = false },
            title = {
                ColumnItem {
                    Text(text = "???????????????????????????", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                }

            },
            confirmButton = {
                TextButton(onClick = {
                    mainViewModel.disconnect()
                    mainViewModel.itemClick = true
                    createAlerter(context) {
                        setTitle("????????????")
                    }

                }) {
                    Text(text = buildAnnotatedString {
                        withStyle(SpanStyle(color = yimahong)) {
                            append(
                                "??????"
                            )
                        }
                    })
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mainViewModel.isDisConnectDialogShow = false
                }) {
                    Text(text = buildAnnotatedString {
                        withStyle(SpanStyle(color = mainViewModel.mainColor)) {
                            append(
                                "??????"
                            )
                        }
                    })
                }
            }
        )
    }
    var isSwipe by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = isSwipe) {
        if (isSwipe) {
            mainViewModel.getConfig()
            delay(2000)
            isSwipe = false
        }
    }
    SwipeRefresh(
        modifier = Modifier.fillMaxSize(),
        state = rememberSwipeRefreshState(isRefreshing = isSwipe),
        onRefresh = { isSwipe = true },
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                scale = true,
                arrowEnabled = true,
                backgroundColor = colorPrimary,
                shape = RoundedCornerShape(50.dp),
                contentColor = Color.White,
                elevation = 0.dp
            )
        }) {
        if (config.config != "" && bannerData.url.isNotEmpty()) {
            LazyColumn(state = listState) {
                item { Spacer(modifier = Modifier.height(20.dp)) }
                item {
                    ColumnItem(modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }) {
                        val bannerState = rememberPagerState()

                        HorizontalPager(
                            count = 2,
                            state = bannerState,
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) { p ->
                            AsyncImage(
                                model = bannerData.url[p],
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "??????????????????",
                            color = dxtitle,
                            fontSize = 20.sp,
                            modifier = Modifier.align(Alignment.Start),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                itemsIndexed(config.server) { i, d ->
                    ColumnItem(modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = mainViewModel.itemClick) {
                            if (mainViewModel.isLogin) {
                                if (mainViewModel.isConnected) {
                                    mainViewModel.itemClick = true
                                    mainViewModel.isDisConnectDialogShow = true
                                } else {
                                    createAlerter(context) {
                                        setTitle("?????????????????????????????????...")
                                    }

                                    mainViewModel.itemClick = false
                                    mainViewModel.connect(config, i)
                                }
                            } else {
                                createAlerter(context) {
                                    setTitle("????????????????????????")
                                }

                            }

                        }) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = d, fontSize = 25.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        } else {
            CircularLoading()
        }
    }
}


@Composable
fun ColumnItem(modifier: Modifier = Modifier, block: @Composable () -> Unit) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        block()
    }
}

@Composable
fun CircularLoading() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = colorPrimary)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "?????????")
    }
}