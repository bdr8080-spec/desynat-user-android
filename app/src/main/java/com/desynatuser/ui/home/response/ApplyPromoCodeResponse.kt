package com.desynatuser.ui.home.response


import com.google.gson.annotations.SerializedName

data class ApplyPromoCodeResponse(
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
        @SerializedName("Apply_discount_value")
        val applyDiscountValue: Double,
        @SerializedName("discount_code")
        val discountCode: String,
        @SerializedName("discount_percentage")
        val discountPercentage: String,
        @SerializedName("final_amount")
        val finalAmount: Double,
        @SerializedName("original_discount")
        val originalDiscount: Double
    )
}