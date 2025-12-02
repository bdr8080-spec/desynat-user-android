package com.desynatuser.ui.home.store.response

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("color_id")
    var colorId: String,
    @SerializedName("color")
    val color: GetSingleStoreResponse.Data.AllProduct.Data.ProductVariant.Color,
    @SerializedName("product_variation")
    val productVariation: ArrayList<ProductVariation>,
    @SerializedName("variant_image")
    val variantImage: ArrayList<String>,
    @SerializedName("product_id")
    val productId: String?=null,
) {

    data class Color(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("hex_code")
        val hex_code: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
    data class ProductVariation(
        @SerializedName("actual_price")
        var actualPrice: Double?=0.0,
        @SerializedName("bust")
        var bust: String?=null,
        @SerializedName("created_at")
        var createdAt: String?=null,
        @SerializedName("discount_type")
        var discountType: String,
        @SerializedName("discount_value")
        var discountValue: String,
        @SerializedName("final_price")
        var finalPrice: Double?=0.0,
        @SerializedName("hip")
        var hip: String?=null,
        @SerializedName("id")
        var id: String?=null,
        @SerializedName("length")
        var length: String?=null,
        @SerializedName("product_id")
        var productId: String?=null,
        @SerializedName("shoulder")
        var shoulder: String?=null,
        @SerializedName("size_id")
        var sizeId: String,
        @SerializedName("size")
        val size: GetSingleStoreResponse.Data.AllProduct.Data.ProductVariant.Size,
        @SerializedName("sku")
        var sku: String,
        @SerializedName("sku_threshold_value")
        var skuThresholdValue: String,
        @SerializedName("sleeve_length")
        var sleeveLength: String?=null,
        @SerializedName("status")
        var status: String?=null,
        @SerializedName("total_stock")
        var totalStock: String,
        @SerializedName("updated_at")
        var updatedAt: String?=null,
        @SerializedName("waist")
        var waist: String?=null
    ){
        data class Size(
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }
}