package com.desynatuser.ui.home.store.response


import com.google.gson.annotations.SerializedName

data class CartQunatityIncrementDecrementResponse(
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
        @SerializedName("id")
        val id: Int,
        @SerializedName("product_id")
        val productId: Int,
        @SerializedName("product_variants")
        val productVariants: ProductVariants,
        @SerializedName("quantity")
        val quantity: Int,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("variants_id")
        val variantsId: Int
    ) {
        data class ProductVariants(
            @SerializedName("actual_price")
            val actualPrice: String,
            @SerializedName("bust")
            val bust: Any,
            @SerializedName("color_id")
            val colorId: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("discount_type")
            val discountType: String,
            @SerializedName("discount_value")
            val discountValue: String,
            @SerializedName("final_price")
            val finalPrice: String,
            @SerializedName("hip")
            val hip: Any,
            @SerializedName("id")
            val id: Int,
            @SerializedName("length")
            val length: Any,
            @SerializedName("product_id")
            val productId: Int,
            @SerializedName("shoulder")
            val shoulder: Any,
            @SerializedName("size_id")
            val sizeId: String,
            @SerializedName("sku")
            val sku: String,
            @SerializedName("sku_threshold_value")
            val skuThresholdValue: String,
            @SerializedName("sleeve_length")
            val sleeveLength: Any,
            @SerializedName("status")
            val status: String,
            @SerializedName("total_stock")
            val totalStock: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("variant_image")
            val variantImage: List<String>,
            @SerializedName("waist")
            val waist: Any
        )
    }
}