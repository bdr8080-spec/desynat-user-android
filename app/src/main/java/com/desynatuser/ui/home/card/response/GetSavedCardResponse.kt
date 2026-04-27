package com.desynatuser.ui.home.card.response


import com.google.gson.annotations.SerializedName

data class GetSavedCardResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("card_holder_name")
        val cardHolderName: String,
        @SerializedName("card_image")
        val cardImage: String,
        @SerializedName("card_number")
        val cardNumber: String,
        @SerializedName("card_type")
        val cardType: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("cvv")
        val cvv: String,
        @SerializedName("expiry_date")
        val expiryDate: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_future")
        val isFuture: Int,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int
    )
}