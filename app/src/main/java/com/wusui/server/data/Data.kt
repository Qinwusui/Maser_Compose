package com.wusui.server.data

import com.google.gson.annotations.SerializedName


//data class PortData(
//    val createdAt: String, // 2022-08-17 08:54:15
//    val objectId: String, // iPBe000H
//    val updatedAt: String, // 2022-08-17 09:04:57
//    val urlName: String // 38614
//)
//
//data class ConfigData(
//    val port: String = "",
//    val domain: String = ""
//)

data class BannerData(
    val name: MutableList<String> = mutableListOf(),
    val url: MutableList<String> = mutableListOf()
)

//data class Version( //可以作为请求Body 也可以作为响应Body 只需要把Current改改就行
//    val current: String, //当前版本
//    val md5: String,
//    val sha1: String
//)

data class Config(
    val config: String = "",
    val pass: MutableList<String> = mutableListOf(),
    @SerializedName("serverlist")
    val server: MutableList<String> = mutableListOf(),
    val users: MutableList<String> = mutableListOf()
)

data class UserBody(
    val code: Int,
    val msg: String = ""
)

data class LoginBody(
    val data: String
)