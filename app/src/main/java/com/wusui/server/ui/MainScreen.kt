package com.wusui.server.ui

import android.annotation.SuppressLint
import android.content.IntentFilter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wusui.server.model.MainViewModel
import com.wusui.server.theme.colorPrimary
import kotlinx.coroutines.launch

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel) {
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current

    LocalBroadcastManager.getInstance(context)
        .registerReceiver(mainViewModel.r, IntentFilter("connectionState"))
    LaunchedEffect(key1 = mainViewModel.mainColor) {
        systemUiController.setStatusBarColor(color = mainViewModel.mainColor, false)
    }
    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()
    val pageList by remember {
        mutableStateOf(mutableListOf("服务器", "我的"))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(), backgroundColor = Color.White,
        topBar = {
            TopAppBar(
                backgroundColor = mainViewModel.mainColor,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp)
                    ) {
                        Text(text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    fontSize = 25.sp,
                                    color = Color.White,
                                    fontFamily = FontFamily.SansSerif
                                )
                            ) {
                                append(mainViewModel.title)
                            }
                            withStyle(SpanStyle(fontSize = 15.sp, color = Color.White)) {
                                append("\nBuild BY 岁岁")
                            }
                        })
                    }
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        backgroundColor = colorPrimary,
                        contentColor = Color.White,
                        indicator = {
                            val modifier = Modifier.tabIndicatorOffset(it[pagerState.currentPage])
                            Canvas(modifier = modifier, onDraw = {

                                drawLine(
                                    Color.White,
                                    start = Offset((this.size.width / 2) - 10f, 0f),
                                    end = Offset((this.size.width / 2) + 10f, 0f),
                                    strokeWidth = 5f
                                )
//                                drawCircle(Color.White, 5f)
                            })
                        },
                        divider = {
                            Divider(
                                modifier = Modifier
                                    .requiredHeight(0.dp)
                                    .requiredWidth(0.dp)
                                    .background(Color.White)
                            )
                        }
                    ) {
                        pageList.forEachIndexed { i, d ->
                            var enabled by remember {
                                mutableStateOf(false)
                            }
                            enabled = pagerState.currentPage == i
                            val size by animateFloatAsState(targetValue = if (enabled) 30f else 15f)

                            Column(
                                modifier = Modifier
                                    .height(40.dp)
                                    .clickable {
                                        enabled = !enabled
                                        scope.launch {
                                            pagerState.animateScrollToPage(i)
                                        }
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = d,
                                    fontSize = size.sp,
                                    color = if (pagerState.currentPage == i) Color.White else Color.Gray,
                                    fontWeight = if (pagerState.currentPage == i) FontWeight.ExtraBold else FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }

    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            count = pageList.size,
            contentPadding = it
        ) { page: Int -> //当前页面
            when (page) {
                0 -> {
                    Server(mainViewModel)
                }
                1 -> {
                    Me(mainViewModel)
                }

            }

        }

    }
}
