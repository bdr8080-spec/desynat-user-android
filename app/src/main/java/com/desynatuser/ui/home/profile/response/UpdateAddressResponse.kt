package com.desynatuser.ui.home.profile.response


import com.google.gson.annotations.SerializedName

data class UpdateAddressResponse(
    @SerializedName("data")
    val `data`: GetAddressResponse.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)