package com.desynatuser.ui.auth.response


import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("confirm_password")
    val confirmPassword: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("password")
    val password: String ,
    @SerializedName("profile_image")
    val profileImage: String?=null
)