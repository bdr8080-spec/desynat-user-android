package com.desynatuser.ui.home.store.response


import com.google.gson.annotations.SerializedName

data class GetSharedReelLinkResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("file")
        val `file`: String,
        @SerializedName("reel_id")
        val reelId: Int
    )
}