package com.desynatuser.ui.home.giftCard.response


import com.google.gson.annotations.SerializedName

data class AddGiftCardRequest(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("gift_card_id")
    val giftCardId: String,
    @SerializedName("notes")
    val notes: String,
    @SerializedName("receiver_email")
    val receiverEmail: String,
    @SerializedName("receiver_name")
    val receiverName: String,
/*    @SerializedName("sender_email")
    val senderEmail: String,*/
    @SerializedName("sender_name")
    val senderName: String,
    @SerializedName("wish_message")
    val wishMessage: String
)