package com.desynatuser.ui.home.giftCard.response


import com.google.gson.annotations.SerializedName

data class ApplyGiftCardResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("GiftCardAmount")
        val giftCardAmount: Double,
        @SerializedName("id")
        val id: Int
    )
}