package com.desynatuser.ui.home.store.response


import com.google.gson.annotations.SerializedName

data class AddToCartListResponse(
    @SerializedName("data")
    val `data`: ArrayList<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("id")
        val id: String,
        @SerializedName("product_id")
        val productId: String,
        @SerializedName("quantity")
        var quantity: Int,
        @SerializedName("variants_id")
        val variantsId: String
    )
}