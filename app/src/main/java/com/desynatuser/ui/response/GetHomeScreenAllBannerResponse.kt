package com.desynatuser.ui.response


import com.google.gson.annotations.SerializedName

data class GetHomeScreenAllBannerResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("Admin_verify")
        val adminVerify: String?=null,
        @SerializedName("seller_details")
        val sellerDetails: SellerDetails?=null,
        @SerializedName("banner_position")
        val bannerPosition: String?=null,
        @SerializedName("cancelled_reason")
        val cancelledReason: String?=null,
        @SerializedName("category")
        val category: String?=null,
        @SerializedName("created_at")
        val createdAt: String?=null,
        @SerializedName("end_date")
        val endDate: String?=null,
        @SerializedName("id")
        val id: String?=null,
        @SerializedName("order_id")
        val orderId: String?=null,
        @SerializedName("payment_status")
        val paymentStatus: String?=null,
        @SerializedName("price")
        val price: String?=null,
        @SerializedName("seller_id")
        val sellerId: String?=null,
        @SerializedName("start_date")
        val startDate: String?=null,
        @SerializedName("status")
        val status: String?=null,
        @SerializedName("total_days")
        val totalDays: String?=null,
        @SerializedName("transaction_id")
        val transactionId: String?=null,
        @SerializedName("updated_at")
        val updatedAt: String?=null,
        @SerializedName("upload_banner")
        val uploadBanner: String?=null
    ) {
        data class CategoryDetails(
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
            val deviceToken: Any,
            @SerializedName("device_type")
            val deviceType: String,
            @SerializedName("email")
            val email: String,
            @SerializedName("fcm_token")
            val fcmToken: Any,
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
            @SerializedName("profile_qr")
            val profileQr: String,
            @SerializedName("qrSellerUrl")
            val qrSellerUrl: String,
            @SerializedName("reject_reason")
            val rejectReason: Any,
            @SerializedName("store_info")
            val storeInfo: StoreInfo,
            @SerializedName("store_information")
            val storeInformation: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class StoreInfo(
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
}