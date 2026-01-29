package com.desynatuser.ui.auth.response


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("accessToken")
        val accessToken: String,
        @SerializedName("affiliated_accessToken")
        val affiliatedAccessToken: String,
        @SerializedName("affiliated_userdetails")
        val affiliatedUserdetails: AffiliatedUserdetails,
        @SerializedName("country_code")
        var countryCode: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("device_token")
        val deviceToken: Any,
        @SerializedName("device_type")
        val deviceType: String,
        @SerializedName("email")
        var email: String,
        @SerializedName("email_verified_at")
        val emailVerifiedAt: Any,
        @SerializedName("full_name")
        val fullName: String,
        @SerializedName("profile_image")
        val profileImage: String?=null,
        @SerializedName("id")
        val id: String,
        @SerializedName("let")
        val let: Any,
        @SerializedName("long")
        val long: Any,
        @SerializedName("mobile")
        var mobile: String,
        @SerializedName("otp")
        val otp: String,
        @SerializedName("otp_verify")
        val otpVerify: String,
        @SerializedName("updated_at")
        val updatedAt: String
    ){
        data class AffiliatedUserdetails(
            @SerializedName("address_update_status")
            val addressUpdateStatus: String,
            @SerializedName("admin_verify")
            val adminVerify: String,
            @SerializedName("bank_update_status")
            val bankUpdateStatus: String,
            @SerializedName("country_code")
            val countryCode: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("device_token")
            val deviceToken: Any,
            @SerializedName("device_type")
            val deviceType: String,
            @SerializedName("document1")
            val document1: String,
            @SerializedName("document2")
            val document2: String,
            @SerializedName("email")
            val email: String,
            @SerializedName("email_verified_at")
            val emailVerifiedAt: Any,
            @SerializedName("full_name")
            val fullName: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("let")
            val let: Any,
            @SerializedName("long")
            val long: Any,
            @SerializedName("mobile")
            val mobile: String,
            @SerializedName("otp")
            val otp: String,
            @SerializedName("otp_verify")
            val otpVerify: String,
            @SerializedName("password")
            val password: String,
            @SerializedName("profile_image")
            val profileImage: String,
            @SerializedName("reject_reason")
            val rejectReason: Any,
            @SerializedName("remember_token")
            val rememberToken: Any,
            @SerializedName("status")
            val status: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: Int
        )
    }
}