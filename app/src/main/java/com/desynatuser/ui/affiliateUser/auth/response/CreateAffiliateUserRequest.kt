package com.desynatuser.ui.affiliateUser.auth.response


import com.google.gson.annotations.SerializedName

data class CreateAffiliateUserRequest(
    @SerializedName("accept")
    val accept: Int,
    @SerializedName("confirm_password")
    val confirmPassword: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("document1")
    val document1: String,
    @SerializedName("document2")
    val document2: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("profile_image")
    val profileImage: String
)