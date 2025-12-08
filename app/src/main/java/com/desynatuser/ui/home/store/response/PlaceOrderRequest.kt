package com.desynatuser.ui.home.store.response


import com.google.gson.annotations.SerializedName

data class PlaceOrderRequest(
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    @SerializedName("discount_mrp")
    val discountMrp: String,
    @SerializedName("giftcard_amount")
    val giftcardAmount: String,
    @SerializedName("Payment_method")
    val paymentMethod: String,// 1 myfatoorah 2 wallet 3 Cod
    @SerializedName("promocode_id")
    val promocodeId: String,
    @SerializedName("promocode_status")// yes no
    val promocodeStatus: String,
    @SerializedName("promocode_value")
    val promocodeValue: String,
    @SerializedName("shipping_fees")
    val shippingFees: String,
    @SerializedName("tax")
    val tax: String,
    @SerializedName("total_amount")
    val totalAmount: String,
    @SerializedName("total_mrp")
    val totalMrp: String
)