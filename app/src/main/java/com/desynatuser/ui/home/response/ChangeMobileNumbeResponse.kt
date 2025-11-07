package com.desynatuser.ui.home.response


import com.google.gson.annotations.SerializedName

data class ChangeMobileNumbeResponse(
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
        @SerializedName("id")
        val id: String,
        @SerializedName("mobile")
        val mobile: String,
        @SerializedName("otp")
        val otp: String,
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