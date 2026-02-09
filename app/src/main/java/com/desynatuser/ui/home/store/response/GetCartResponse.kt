package com.desynatuser.ui.home.store.response


import com.google.gson.annotations.SerializedName

data class GetCartResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
{
    data class Data(
        @SerializedName("cart_data")
        val cartData: List<CartData>,
        @SerializedName("discount_coupon")
        val discountCoupon: Double,
        @SerializedName("discount_on_mrp")
        val discountOnMrp: Double,
        @SerializedName("shipping_fees")
        val shippingFees: Double,
        @SerializedName("tax")
        val tax: Double,
        @SerializedName("total_amount")
        val totalAmount: Double,
        @SerializedName("total_mrp")
        val totalMrp: Double,
        @SerializedName("user_address")
        val userAddress: UserAddress
    ) {
        data class CartData(
            @SerializedName("amount")
            val amount: Double,
            @SerializedName("check_stock")
            val checkStock: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("order_type")
            val orderType: String,
            @SerializedName("product")
            val product: Product,
            @SerializedName("product_id")
            val productId: String,
            @SerializedName("product_variants")
            val productVariants: ProductVariants,
            @SerializedName("quantity")
            val quantity: Int,
            @SerializedName("shipping_fees")
            val shippingFees: Double,
            @SerializedName("tax")
            val tax: Double,
            @SerializedName("unique_id")
            val uniqueId: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: String,
            @SerializedName("variants_id")
            val variantsId: String,
            @SerializedName("chatroom_data")
            val chatroomData: ChatroomData
        ) {
            data class ChatroomData(
                @SerializedName("action")
                val action: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("custom_details")
                val customDetails: String,
                @SerializedName("delivery_address")
                val deliveryAddress: String,
                @SerializedName("id")
                val id: String,
                @SerializedName("price")
                val price: String?,
                @SerializedName("product_id")
                val productId: String,
                @SerializedName("product_request")
                val productRequest: String,
                @SerializedName("quantity")
                val quantity: String,
                @SerializedName("seller_id")
                val sellerId: Int,
                @SerializedName("shipping")
                val shipping: String,
                @SerializedName("tax")
                val tax: String,
                @SerializedName("total_amount")
                val totalAmount: String,
                @SerializedName("type")
                val type: String,
                @SerializedName("unique_id")
                val uniqueId: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("user_id")
                val userId: Int,
                @SerializedName("variation_id")
                val variationId: String
            )
            data class Product(
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
                val id: String,
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
                @SerializedName("ready_to_ship")
                val readyToShip: String,
                @SerializedName("return_days")
                val returnDays: String,
                @SerializedName("review_rating")
                val reviewRating: String,
                @SerializedName("seller_details")
                val sellerDetails: SellerDetails,
                @SerializedName("seller_id")
                val sellerId: String,
                @SerializedName("store")
                val store: Store,
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
                    @SerializedName("affilate_commission")
                    val affilateCommission: String,
                    @SerializedName("bank_information")
                    val bankInformation: String,
                    @SerializedName("country_code")
                    val countryCode: String,
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("device_token")
                    val deviceToken: String,
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
                    val lang: String,
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
                    val id: String,
                    @SerializedName("let")
                    val let: String,
                    @SerializedName("long")
                    val long: String,
                    @SerializedName("seller_id")
                    val sellerId: String,
                    @SerializedName("store_address")
                    val storeAddress: String,
                    @SerializedName("Store_Availability")
                    val storeAvailability: String,
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
                val id: String,
                @SerializedName("length")
                val length: String,
                @SerializedName("parent_child")
                val parentChild: String,
                @SerializedName("product_id")
                val productId: String,
                @SerializedName("shoulder")
                val shoulder: String,
                @SerializedName("size")
                val size: Size,
                @SerializedName("size_id")
                val sizeId: String,
                @SerializedName("sku")
                val sku: String,
                @SerializedName("sku_threshold_value")
                val skuThresholdValue: String,
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
                    val id: String,
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )

                data class Size(
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("id")
                    val id: String,
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )
            }
        }

        data class UserAddress(
            @SerializedName("address")
            val address: String,
            @SerializedName("city")
            val city: String,
            @SerializedName("country_code")
            val countryCode: String,
            @SerializedName("country_name")
            val countryName: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("house_no")
            val houseNo: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("landmark")
            val landmark: String,
            @SerializedName("latitude")
            val latitude: String,
            @SerializedName("longitude")
            val longitude: String,
            @SerializedName("mobile")
            val mobile: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("postal_code")
            val postalCode: String,
            @SerializedName("save_address_as")
            val saveAddressAs: String,
            @SerializedName("set_as_default")
            val setAsDefault: String,
            @SerializedName("state")
            val state: String,
            @SerializedName("status")
            val status: String,
            @SerializedName("street_no")
            val streetNo: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: String
        )
    }
}