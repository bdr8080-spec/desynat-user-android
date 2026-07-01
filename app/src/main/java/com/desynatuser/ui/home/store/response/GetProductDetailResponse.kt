package com.desynatuser.ui.home.store.response


import com.google.gson.annotations.SerializedName

data class GetProductDetailResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("addtocart")
        val addtocart: Addtocart,
        @SerializedName("affiliated_product")
        val affiliatedProduct: String,
        @SerializedName("brand")
        val brand: Brand,
        @SerializedName("brand_id")
        val brandId: String,
        @SerializedName("cash_on_delivery")
        val cashOnDelivery: String,
        @SerializedName("category")
        val category: Category,
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
        val otherAr: String,
        @SerializedName("other_en")
        val otherEn: String,
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
        @SerializedName("product_specifications")
        val productSpecifications: List<GetSingleStoreResponse.Data.AllProduct.Data.ProductSpecification>,
        @SerializedName("product_variants")
        val productVariants: ArrayList<GetSingleStoreResponse.Data.AllProduct.Data.ProductVariant>,
        @SerializedName("similar_products")
        val similarProducts: List<SimilarProduct>,
        @SerializedName("ready_to_ship")
        val readyToShip: String,
        @SerializedName("return_days")
        val returnDays: String,
        @SerializedName("review_rating")
        val reviewRating: String,
        @SerializedName("seller_id")
        val sellerId: String,
        @SerializedName("store")
        val store: Store,
        @SerializedName("store_id")
        val storeId: String,
        @SerializedName("subcategory")
        val subcategory: Subcategory,
        @SerializedName("subcategory_id")
        val subcategoryId: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("upload_size_chart")
        val uploadSizeChart: String,
        @SerializedName("wishlist")
        var wishlist: Int,
        @SerializedName("average_rating")
        val productAvgRating: String,
        @SerializedName("review")
        val review: List<Review>,
    ) {
        data class SimilarProduct(
            @SerializedName("affiliated_commission")
            val affiliatedCommission: Any,
            @SerializedName("affiliated_product")
            val affiliatedProduct: String,
            @SerializedName("brand")
            val brand: Brand,
            @SerializedName("brand_id")
            val brandId: String,
            @SerializedName("cash_on_delivery")
            val cashOnDelivery: String,
            @SerializedName("category")
            val category: Category,
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
            @SerializedName("product_avg_rating")
            val productAvgRating: Any,
            @SerializedName("product_exchangable")
            val productExchangable: String,
            @SerializedName("product_name_ar")
            val productNameAr: String,
            @SerializedName("product_name_en")
            val productNameEn: String,
            @SerializedName("product_return")
            val productReturn: String,
            @SerializedName("product_specifications")
            val productSpecifications: List<ProductSpecification>,
            @SerializedName("product_variants")
            val productVariants: List<ProductVariant>,
            @SerializedName("ready_to_ship")
            val readyToShip: String,
            @SerializedName("return_days")
            val returnDays: String,
            @SerializedName("review_count")
            val reviewCount: Int,
            @SerializedName("review_rating")
            val reviewRating: String,
            @SerializedName("seller_id")
            val sellerId: String,
            @SerializedName("store_id")
            val storeId: String,
            @SerializedName("subcategory")
            val subcategory: Subcategory,
            @SerializedName("subcategory_id")
            val subcategoryId: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("upload_size_chart")
            val uploadSizeChart: String
        )
        {
            data class Brand(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("image")
                val image: String,
                @SerializedName("name_arabic")
                val nameArabic: String,
                @SerializedName("name_english")
                val nameEnglish: String,
                @SerializedName("status")
                val status: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class Category(
                @SerializedName("color_code")
                val colorCode: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("description")
                val description: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("image")
                val image: String,
                @SerializedName("name")
                val name: String,
                @SerializedName("name_ar")
                val nameAr: String,
                @SerializedName("status")
                val status: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class ProductSpecification(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("feature_ar")
                val featureAr: String,
                @SerializedName("feature_en")
                val featureEn: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("name_ar")
                val nameAr: String,
                @SerializedName("name_en")
                val nameEn: String,
                @SerializedName("product_id")
                val productId: String,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class ProductVariant(
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
                @SerializedName("parent_child")
                val parentChild: String,
                @SerializedName("product_id")
                val productId: Int,
                @SerializedName("shoulder")
                val shoulder: String,
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
                    @SerializedName("name_ar")
                    val nameAr: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )
            }

            data class Subcategory(
                @SerializedName("category_id")
                val categoryId: Int,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("image")
                val image: String,
                @SerializedName("name_arabic")
                val nameArabic: String,
                @SerializedName("name_english")
                val nameEnglish: String,
                @SerializedName("status")
                val status: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            )
        }

        data class Review(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("order_id")
            val orderId: Int,
            @SerializedName("orderdetails_id")
            val orderdetailsId: Int,
            @SerializedName("product_id")
            val productId: Int,
            @SerializedName("product_image")
            val productImage: String,
            @SerializedName("product_rating")
            val productRating: Float,
            @SerializedName("product_review")
            val productReview: String,
            @SerializedName("store_id")
            val storeId: Int,
            @SerializedName("store_image")
            val storeImage: String,
            @SerializedName("store_rating")
            val storeRating: Float,
            @SerializedName("store_review")
            val storeReview: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_details")
            val userDetails: UserDetails,
            @SerializedName("user_id")
            val userId: Int
        ){
            data class UserDetails(
                @SerializedName("country_code")
                val countryCode: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("device_token")
                val deviceToken: Any,
                @SerializedName("device_type")
                val deviceType: String,
                @SerializedName("email")
                val email: String,
                @SerializedName("email_verified_at")
                val emailVerifiedAt: Any,
                @SerializedName("full_name")
                val fullName: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("let")
                val let: Any,
                @SerializedName("long")
                val long: Any,
                @SerializedName("mobile")
                val mobile: String,
                @SerializedName("otp")
                val otp: String,
                @SerializedName("otp_verify")
                val otpVerify: String,
                @SerializedName("profile_image")
                val profileImage: Any,
                @SerializedName("status")
                val status: String,
                @SerializedName("updated_at")
                val updatedAt: String
            )
        }
        data class Addtocart(
            @SerializedName("amount")
            val amount: String? = null,
            @SerializedName("created_at")
            val createdAt: String? = null,
            @SerializedName("id")
            val id: String? = null,
            @SerializedName("product_id")
            val productId: String? = null,
            @SerializedName("quantity")
            val quantity: Int? = null,
            @SerializedName("updated_at")
            val updatedAt: String? = null,
            @SerializedName("user_id")
            val userId: String? = null,
            @SerializedName("variants_id")
            val variantsId: String? = null
        )

        data class Brand(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("image")
            val image: String,
            @SerializedName("name_arabic")
            val nameArabic: String,
            @SerializedName("name_english")
            val nameEnglish: String,
            @SerializedName("status")
            val status: Int,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class Category(
            @SerializedName("color_code")
            val colorCode: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("description")
            val description: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("image")
            val image: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("status")
            val status: Int,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class ProductSpecification(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("feature_ar")
            val featureAr: String,
            @SerializedName("feature_en")
            val featureEn: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("name_ar")
            val nameAr: String,
            @SerializedName("name_en")
            val nameEn: String,
            @SerializedName("product_id")
            val productId: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class ProductVariant(
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
            @SerializedName("parent_child")
            val parentChild: String,
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

        data class Store(
            @SerializedName("address_line1")
            val addressLine1: String,
            @SerializedName("address_line2")
            val addressLine2: String,
            @SerializedName("bio")
            val bio: String,
            @SerializedName("country")
            val country: String,
            @SerializedName("country_code")
            val countryCode: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("let")
            val let: String,
            @SerializedName("long")
            val long: String,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("store_address")
            val storeAddress: String,
            @SerializedName("Store_Availability")
            val storeAvailability: Int,
            @SerializedName("store_image")
            val storeImage: String,
            @SerializedName("store_mail_id")
            val storeMailId: String,
            @SerializedName("store_name")
            val storeName: String,
            @SerializedName("store_phone")
            val storePhone: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class Subcategory(
            @SerializedName("category_id")
            val categoryId: Int,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("image")
            val image: String,
            @SerializedName("name_arabic")
            val nameArabic: String,
            @SerializedName("name_english")
            val nameEnglish: String,
            @SerializedName("status")
            val status: Int,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }
}