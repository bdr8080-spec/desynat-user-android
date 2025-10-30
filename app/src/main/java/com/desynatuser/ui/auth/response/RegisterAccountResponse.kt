package com.desynatuser.ui.auth.response


import com.google.gson.annotations.SerializedName

data class RegisterAccountResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("country_code")
        val countryCode: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("full_name")
        val fullName: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("mobile")
        val mobile: String,
        @SerializedName("otp")
        val otp: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}