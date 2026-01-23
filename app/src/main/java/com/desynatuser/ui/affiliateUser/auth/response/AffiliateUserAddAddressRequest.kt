package com.desynatuser.ui.affiliateUser.auth.response


import com.google.gson.annotations.SerializedName

data class AffiliateUserAddAddressRequest(
    @SerializedName("address1")
    val address1: String,
    @SerializedName("address2")
    val address2: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("let")
    val let: Double,
    @SerializedName("locations")
    val locations: String,
    @SerializedName("long")
    val long: Double,
    @SerializedName("pincode")
    val pincode: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("town")
    val town: String
)