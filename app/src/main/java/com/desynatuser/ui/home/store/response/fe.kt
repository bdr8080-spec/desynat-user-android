package com.desynatuser.ui.home.store.response


import com.google.gson.annotations.SerializedName

data class fe(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("cart_data")
        val cartData: List<CartData>,
        @SerializedName("discount_coupon")
        val discountCoupon: Int,
        @SerializedName("discount_on_mrp")
        val discountOnMrp: Int,
        @SerializedName("shipping_fees")
        val shippingFees: Int,
        @SerializedName("tax")
        val tax: Int,
        @SerializedName("total_amount")
        val totalAmount: Int,
        @SerializedName("total_mrp")
        val totalMrp: Int
    ) {
        data class CartData(
            @SerializedName("amount")
            val amount: String,
            @SerializedName("check_stock")
            val checkStock: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("product")
            val product: Product,
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
            data class Product(
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
                val exchangableDays: Any,
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
                val returnDays: Any,
                @SerializedName("review_rating")
                val reviewRating: String,
                @SerializedName("seller_details")
                val sellerDetails: SellerDetails,
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
            ) {
                data class SellerDetails(
                    @SerializedName("accept_terms")
                    val acceptTerms: String,
                    @SerializedName("admin_verify")
                    val adminVerify: String,
                    @SerializedName("bank_information")
                    val bankInformation: String,
                    @SerializedName("country_code")
                    val countryCode: String,
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("device_token")
                    val deviceToken: Any,
                    @SerializedName("device_type")
                    val deviceType: Any,
                    @SerializedName("email")
                    val email: String,
                    @SerializedName("first_name")
                    val firstName: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("is_block")
                    val isBlock: Int,
                    @SerializedName("lang")
                    val lang: Any,
                    @SerializedName("last_name")
                    val lastName: String,
                    @SerializedName("mobile")
                    val mobile: String,
                    @SerializedName("otp")
                    val otp: String,
                    @SerializedName("otp_verify")
                    val otpVerify: String,
                    @SerializedName("password")
                    val password: String,
                    @SerializedName("profile_pic")
                    val profilePic: String,
                    @SerializedName("reject_reason")
                    val rejectReason: Any,
                    @SerializedName("store_information")
                    val storeInformation: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )
            }

            data class ProductVariants(
                @SerializedName("actual_price")
                val actualPrice: String,
                @SerializedName("bust")
                val bust: String,
                @SerializedName("color")
                val color: Color,
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
                val hip: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("length")
                val length: String,
                @SerializedName("product_id")
                val productId: Int,
                @SerializedName("shoulder")
                val shoulder: String,
                @SerializedName("size")
                val size: Size,
                @SerializedName("size_id")
                val sizeId: String,
                @SerializedName("sku")
                val sku: String,
                @SerializedName("sku_threshold_value")
                val skuThresholdValue: Any,
                @SerializedName("sleeve_length")
                val sleeveLength: String,
                @SerializedName("status")
                val status: String,
                @SerializedName("total_stock")
                val totalStock: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("variant_image")
                val variantImage: List<String>,
                @SerializedName("waist")
                val waist: String
            ) {
                data class Color(
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("hex_code")
                    val hexCode: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )

                data class Size(
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )
            }
        }
    }
}