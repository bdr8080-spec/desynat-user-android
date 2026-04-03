package com.desynatuser.ui.home.storyHighlights.response


import com.google.gson.annotations.SerializedName

data class GetAgoraTokenResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("appId")
        val appId: String,
        @SerializedName("channel")
        val channel: String,
        @SerializedName("token")
        val token: String,
        @SerializedName("uid")
        val uid: String
    )
}