package com.desynatuser.ui.home.chat.response


import com.google.gson.annotations.SerializedName

data class NormalChatResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("action")
        val action: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("custom_details")
        val customDetails: Any,
        @SerializedName("delivery_address")
        val deliveryAddress: Any,
        @SerializedName("id")
        val id: Int,
        @SerializedName("price")
        val price: Any,
        @SerializedName("product_id")
        val productId: Any,
        @SerializedName("product_request")
        val productRequest: String,
        @SerializedName("product_variation")
        val productVariation: Any,
        @SerializedName("quantity")
        val quantity: Any,
        @SerializedName("seller_id")
        val sellerId: Int,
        @SerializedName("shipping")
        val shipping: Any,
        @SerializedName("tax")
        val tax: Any,
        @SerializedName("total_amount")
        val totalAmount: Any,
        @SerializedName("type")
        val type: String,
        @SerializedName("unique_id")
        val uniqueId: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("variation_id")
        val variationId: Any
    )
}