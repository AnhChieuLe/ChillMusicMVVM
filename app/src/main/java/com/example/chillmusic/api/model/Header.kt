package com.example.chillmusic.api.model

import com.google.gson.annotations.SerializedName

data class Header(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("execute_time")
    val executeTime: Float,
    @SerializedName("available")
    val available: Int
)