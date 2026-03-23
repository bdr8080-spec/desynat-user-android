package com.desynatuser.ui.home.fragment.response


import com.google.gson.annotations.SerializedName

data class GetDealBannerResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("category_id")
        val categoryId: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("discount")
        val discount: String,
        @SerializedName("from")
        val from: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("image")
        val image: String,
        @SerializedName("name_arabic")
        val nameArabic: String,
        @SerializedName("name_english")
        val nameEnglish: String,
        @SerializedName("seller_dealrecord")
        val sellerDealrecord: List<SellerDealrecord>,
        @SerializedName("status")
        val status: Int,
        @SerializedName("sub_category_id")
        val subCategoryId: List<String>,
        @SerializedName("to")
        val to: String,
        @SerializedName("updated_at")
        val updatedAt: String
    ) {
        data class SellerDealrecord(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("deal_id")
            val dealId: Int,
            @SerializedName("discount")
            val discount: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("product_details")
            val productDetails: ProductDetails,
            @SerializedName("product_id")
            val productId: Int,
            @SerializedName("seller_id")
            val sellerId: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class ProductDetails(
                @SerializedName("affiliated_commission")
                val affiliatedCommission: String,
                @SerializedName("affiliated_product")
                val affiliatedProduct: String,
                @SerializedName("brand_id")
                val brandId: String,
                @SerializedName("cash_on_delivery")
                val cashOnDelivery: String,
                @SerializedName("category_id")
                val categoryId: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("customized_product")
                val customizedProduct: String,
                @SerializedName("description_ar")
                val descriptionAr: String,
                @SerializedName("description_en")
                val descriptionEn: String,
                @SerializedName("exchangable_days")
                val exchangableDays: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("max_card_quantity")
                val maxCardQuantity: String,
                @SerializedName("min_card_quantity")
                val minCardQuantity: String,
                @SerializedName("other_ar")
                val otherAr: Any,
                @SerializedName("other_en")
                val otherEn: Any,
                @SerializedName("product_active_status")
                val productActiveStatus: Int,
                @SerializedName("product_exchangable")
                val productExchangable: String,
                @SerializedName("product_name_ar")
                val productNameAr: String,
                @SerializedName("product_name_en")
                val productNameEn: String,
                @SerializedName("product_return")
                val productReturn: String,
                @SerializedName("ready_to_ship")
                val readyToShip: String,
                @SerializedName("return_days")
                val returnDays: String,
                @SerializedName("review_rating")
                val reviewRating: String,
                @SerializedName("seller_id")
                val sellerId: String,
                @SerializedName("store_id")
                val storeId: String,
                @SerializedName("subcategory_id")
                val subcategoryId: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("upload_size_chart")
                val uploadSizeChart: String
            )
        }
    }
}