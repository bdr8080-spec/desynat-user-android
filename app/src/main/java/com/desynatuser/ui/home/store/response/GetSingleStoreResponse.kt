package com.desynatuser.ui.home.store.response


import com.google.gson.annotations.SerializedName

data class GetSingleStoreResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("All_Product")
        val allProduct: AllProduct,
        @SerializedName("store")
        val store: Store
    ) {
        data class AllProduct(
            @SerializedName("current_page")
            val currentPage: Int,
            @SerializedName("data")
            val `data`: ArrayList<Data>,
            @SerializedName("first_page_url")
            val firstPageUrl: String,
            @SerializedName("from")
            val from: Int,
            @SerializedName("last_page")
            val lastPage: Int,
            @SerializedName("last_page_url")
            val lastPageUrl: String,
            @SerializedName("links")
            val links: List<Link>,
            @SerializedName("next_page_url")
            val nextPageUrl: Any,
            @SerializedName("path")
            val path: String,
            @SerializedName("per_page")
            val perPage: Int,
            @SerializedName("prev_page_url")
            val prevPageUrl: Any,
            @SerializedName("to")
            val to: Int,
            @SerializedName("total")
            val total: Int
        ) {
            data class Data(
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
                val productSpecifications: ArrayList<ProductSpecification>,
                @SerializedName("product_variants")
                val productVariants: ArrayList<ProductVariant>,
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
                @SerializedName("subcategory")
                val subcategory: Subcategory,
                @SerializedName("subcategory_id")
                val subcategoryId: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("upload_size_chart")
                val uploadSizeChart: String,
                @SerializedName("wishlist")
                val wishlist: Int
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
                    @SerializedName("size")
                    val size: Size,
                    @SerializedName("color")
                    val color: Color,
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
                    val variantImage: ArrayList<String>,
                    @SerializedName("waist")
                    val waist: Any
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

            data class Link(
                @SerializedName("active")
                val active: Boolean,
                @SerializedName("label")
                val label: String,
                @SerializedName("url")
                val url: String
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
    }
}