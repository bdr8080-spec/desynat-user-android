package com.desynatuser.ui.affiliateUser.home.profile.response


import com.google.gson.annotations.SerializedName

data class AffiliateChangeUserOtpVerifyResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("change_table")
        val changeTable: String,
        @SerializedName("country_code")
        val countryCode: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("mobile")
        val mobile: String,
        @SerializedName("otp")
        val otp: Any,
        @SerializedName("otp_verify")
        val otpVerify: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int
    )
}