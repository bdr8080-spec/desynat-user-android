package com.desynatuser.ui.affiliateUser.home.product.response


import com.google.gson.annotations.SerializedName

data class GetAffiliateMyProductDetailResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
{
    data class Data(
        @SerializedName("affiliate_commission")
        val affiliateCommission: Int,
        @SerializedName("affiliate_details")
        val affiliateDetails: AffiliateDetails,
        @SerializedName("affiliate_id")
        val affiliateId: Int,
        @SerializedName("affiliate_product_click_records")
        val affiliateProductClickRecords: List<AffiliateProductClickRecord>,
        @SerializedName("affiliate_product_click_records_count")
        val affiliateProductClickRecordsCount: Int,
        @SerializedName("affiliate_product_link")
        val affiliateProductLink: AffiliateProductLink,
        @SerializedName("average_rating")
        val averageRating: AverageRating,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("my_earning")
        val myEarning: String,
        @SerializedName("conversionrate")
        val conversionrate: String,
        @SerializedName("product")
        val product: Product,
        @SerializedName("product_id")
        val productId: Int,
        @SerializedName("Total_order")
        val totalOrder: Int,
        @SerializedName("Total_revenue")
        val totalRevenue: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
    {
        data class AffiliateDetails(
            @SerializedName("address_update_status")
            val addressUpdateStatus: String,
            @SerializedName("admin_verify")
            val adminVerify: String,
            @SerializedName("bank_update_status")
            val bankUpdateStatus: String,
            @SerializedName("country_code")
            val countryCode: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("device_token")
            val deviceToken: Any,
            @SerializedName("device_type")
            val deviceType: String,
            @SerializedName("document1")
            val document1: String,
            @SerializedName("document2")
            val document2: String,
            @SerializedName("email")
            val email: String,
            @SerializedName("email_verified_at")
            val emailVerifiedAt: Any,
            @SerializedName("full_name")
            val fullName: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("lang")
            val lang: String,
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
            @SerializedName("password")
            val password: String,
            @SerializedName("password_old")
            val passwordOld: String,
            @SerializedName("profile_image")
            val profileImage: String,
            @SerializedName("reject_reason")
            val rejectReason: Any,
            @SerializedName("remember_token")
            val rememberToken: Any,
            @SerializedName("status")
            val status: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: Int
        )

        data class AffiliateProductClickRecord(
            @SerializedName("affiliate_id")
            val affiliateId: Int,
            @SerializedName("affiliate_product_id")
            val affiliateProductId: Int,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("product_id")
            val productId: Int,
            @SerializedName("reference_link")
            val referenceLink: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: Any
        )

        data class AffiliateProductLink(
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

        data class AverageRating(
            @SerializedName("product")
            val product: Int,
            @SerializedName("store")
            val store: Int
        )

        data class Product(
            @SerializedName("affiliated_commission")
            val affiliatedCommission: String,
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
            @SerializedName("review")
            val review: List<Review>,
            @SerializedName("review_count")
            val reviewCount: Int,
            @SerializedName("review_rating")
            val reviewRating: String,
            @SerializedName("seller_details")
            val sellerDetails: SellerDetails,
            @SerializedName("seller_id")
            val sellerId: String,
            @SerializedName("store")
            val store: GetAffiliateAllProductResponse.Data.Data.Store,
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
        ) {
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
                val bust: Any,
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
                val hip: Any,
                @SerializedName("id")
                val id: Int,
                @SerializedName("length")
                val length: Any,
                @SerializedName("parent_child")
                val parentChild: String,
                @SerializedName("product_id")
                val productId: Int,
                @SerializedName("shoulder")
                val shoulder: Any,
                @SerializedName("size")
                val size: Size,
                @SerializedName("size_id")
                val sizeId: String,
                @SerializedName("sku")
                val sku: String,
                @SerializedName("sku_threshold_value")
                val skuThresholdValue: Any,
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
                val productImage: List<String>?,
                @SerializedName("product_rating")
                val productRating: String,
                @SerializedName("product_review")
                val productReview: String,
                @SerializedName("store_id")
                val storeId: Int,
                @SerializedName("store_image")
                val storeImage: List<String>?,
                @SerializedName("store_rating")
                val storeRating: String,
                @SerializedName("store_review")
                val storeReview: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("user_id")
                val userId: Int
            )

            data class SellerDetails(
                @SerializedName("accept_terms")
                val acceptTerms: String,
                @SerializedName("admin_verify")
                val adminVerify: String,
                @SerializedName("affilate_commission")
                val affilateCommission: String,
                @SerializedName("bank_information")
                val bankInformation: String,
                @SerializedName("country_code")
                val countryCode: String,
                @SerializedName("country_details")
                val countryDetails: CountryDetails,
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
            ) {
                data class CountryDetails(
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("currency_code")
                    val currencyCode: String,
                    @SerializedName("currency_name")
                    val currencyName: String,
                    @SerializedName("currency_symbol")
                    val currencySymbol: String,
                    @SerializedName("dial_code")
                    val dialCode: String,
                    @SerializedName("exchange_rate_to_kwd")
                    val exchangeRateToKwd: String,
                    @SerializedName("flag")
                    val flag: String,
                    @SerializedName("flag1")
                    val flag1: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("iso_code")
                    val isoCode: String,
                    @SerializedName("name")
                    val name: String,
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
    }
}