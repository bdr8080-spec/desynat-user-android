package com.desynatuser.ui.home.chat.response


import com.google.gson.annotations.SerializedName

data class AcceptCustomOrderResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("delivery_address")
        val deliveryAddress: Int,
        @SerializedName("discount_mrp")
        val discountMrp: Int,
        @SerializedName("giftcard_amount")
        val giftcardAmount: String,
        @SerializedName("giftcard_id")
        val giftcardId: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("order_id")
        val orderId: String,
        @SerializedName("order_type")
        val orderType: String,
        @SerializedName("Payment_method")
        val paymentMethod: String,
        @SerializedName("promocode_id")
        val promocodeId: Any,
        @SerializedName("promocode_status")
        val promocodeStatus: String,
        @SerializedName("promocode_value")
        val promocodeValue: Any,
        @SerializedName("shipping_fees")
        val shippingFees: String,
        @SerializedName("tax")
        val tax: String,
        @SerializedName("total_amount")
        val totalAmount: String,
        @SerializedName("total_mrp")
        val totalMrp: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int
    )
}