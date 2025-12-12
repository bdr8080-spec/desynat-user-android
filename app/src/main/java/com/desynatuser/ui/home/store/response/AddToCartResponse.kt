package com.desynatuser.ui.home.store.response


import com.google.gson.annotations.SerializedName

data class AddToCartResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("product_id")
        val productId: String,
        @SerializedName("quantity")
        val quantity: Int,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("variants_id")
        val variantsId: String
    )
}