package com.desynatuser.ui.home.fragment.response


import com.google.gson.annotations.SerializedName

data class GetAffiliateNotificationCountResponse(
    @SerializedName("data")
    val `data`: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)