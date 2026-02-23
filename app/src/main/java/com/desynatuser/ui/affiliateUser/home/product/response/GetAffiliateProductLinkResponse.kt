package com.desynatuser.ui.affiliateUser.home.product.response


import com.google.gson.annotations.SerializedName

data class GetAffiliateProductLinkResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("affiliate_id")
        val affiliateId: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("link")
        val link: String,
        @SerializedName("product_id")
        val productId: Int,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}