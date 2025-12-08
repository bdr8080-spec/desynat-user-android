package com.desynatuser.ui.home.giftCard.response


import com.google.gson.annotations.SerializedName

data class BuyGiftCardResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("amount")
        val amount: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("gift_card_id")
        val giftCardId: String,
        @SerializedName("giftCardNo")
        val giftCardNo: Long,
        @SerializedName("giftCardPin")
        val giftCardPin: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("notes")
        val notes: String,
        @SerializedName("order_id")
        val orderId: String,
        @SerializedName("receiver_email")
        val receiverEmail: String,
        @SerializedName("receiver_id")
        val receiverId: Int,
        @SerializedName("receiver_name")
        val receiverName: String,
        @SerializedName("sender_email")
        val senderEmail: String,
        @SerializedName("sender_name")
        val senderName: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("wish_message")
        val wishMessage: String
    )
}