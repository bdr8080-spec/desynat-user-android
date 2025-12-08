package com.desynatuser.ui.home.giftCard.response


import com.google.gson.annotations.SerializedName

data class GiftCardCompletePaymentResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("amount")
        val amount: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("gift_card_id")
        val giftCardId: Int,
        @SerializedName("giftCardNo")
        val giftCardNo: String,
        @SerializedName("giftCardPin")
        val giftCardPin: String,
        @SerializedName("giftCardUse")
        val giftCardUse: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("notes")
        val notes: String,
        @SerializedName("order_id")
        val orderId: String,
        @SerializedName("payment_status")
        val paymentStatus: String,
        @SerializedName("receiver_email")
        val receiverEmail: String,
        @SerializedName("receiver_id")
        val receiverId: String,
        @SerializedName("receiver_name")
        val receiverName: String,
        @SerializedName("sender_email")
        val senderEmail: String,
        @SerializedName("sender_name")
        val senderName: String,
        @SerializedName("transaction_id")
        val transactionId: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("wish_message")
        val wishMessage: String
    )
}