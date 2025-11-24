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
        val amount: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("product_id")
        val productId: Int,
        @SerializedName("quantity")
        val quantity: Int,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("variants_id")
        val variantsId: Int
    )
}