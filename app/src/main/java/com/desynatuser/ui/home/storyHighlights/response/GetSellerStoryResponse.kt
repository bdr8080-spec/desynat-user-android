package com.desynatuser.ui.home.storyHighlights.response


import com.google.gson.annotations.SerializedName

data class GetSellerStoryResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(

        @SerializedName("highlights")
        val highlights: List<SellerHighlight>? = emptyList(),
        @SerializedName("title")
        val title: String? = "",

        @SerializedName("created_at")
        val createdAt: String? = "",
        @SerializedName("file")
        val `file`: String? = "",
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_seen")
        var isSeen: Int = 0,
        @SerializedName("seller_details")
        val sellerDetails: SellerDetails? = null,
        @SerializedName("seller_id")
        val sellerId: Int? = null,
        @SerializedName("status")
        val status: Int? = 0,
        @SerializedName("type")
        val type: Int? = 0,
        @SerializedName("updated_at")
        val updatedAt: String? = "",
        @SerializedName("caption")
        val caption: String? = "",
        @SerializedName("is_whishlisted")
        var wishlist: Int? = 0
    ) {

        data class SellerHighlight(

            @SerializedName("id")
            val id: Int,

            @SerializedName("type")
            val type: Int,

            @SerializedName("seller_id")
            val sellerId: Int? = null,

            @SerializedName("file")
            val file: String? = "",

            @SerializedName("title")
            val title: String? = "",

            @SerializedName("status")
            val status: Int? = 0,

            @SerializedName("thumbnail")
            val thumbnail: String? = "",

            @SerializedName("created_at")
            val createdAt: String? = "",

            @SerializedName("updated_at")
            val updatedAt: String? = "",

            @SerializedName("is_seen")
            var isSeen: Int = 0,

            @SerializedName("caption")
            val caption: String? = "",

            )

        data class SellerDetails(
            @SerializedName("accept_terms")
            val acceptTerms: String?,
            @SerializedName("admin_verify")
            val adminVerify: String?,
            @SerializedName("affilate_commission")
            val affilateCommission: String?,
            @SerializedName("bank_information")
            val bankInformation: String?,
            @SerializedName("country_code")
            val countryCode: String?,
            @SerializedName("country_details")
            val countryDetails: Any?,
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("device_token")
            val deviceToken: String?,
            @SerializedName("device_type")
            val deviceType: String?,
            @SerializedName("email")
            val email: String?,
            @SerializedName("fcm_token")
            val fcmToken: String?,
            @SerializedName("first_name")
            val firstName: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("is_block")
            val isBlock: Int?,
            @SerializedName("lang")
            val lang: Any?,
            @SerializedName("last_name")
            val lastName: String?,
            @SerializedName("mobile")
            val mobile: String?,
            @SerializedName("otp")
            val otp: String?,
            @SerializedName("otp_verify")
            val otpVerify: String?,
            @SerializedName("password")
            val password: String?,
            @SerializedName("profile_pic")
            val profilePic: String?,
            @SerializedName("profile_qr")
            val profileQr: String?,
            @SerializedName("qrSellerUrl")
            val qrSellerUrl: String?,
            @SerializedName("reject_reason")
            val rejectReason: Any?,
            @SerializedName("store_information")
            val storeInformation: String?,
            @SerializedName("updated_at")
            val updatedAt: String?,
            @SerializedName("store_info")
            val storeInfo: StoreInfo?
        ) {
            data class StoreInfo(
                @SerializedName("id")
                val id: Int?,
                @SerializedName("seller_id")
                val sellerId: Int?,
                @SerializedName("store_image")
                val storeImage: String?,
                @SerializedName("store_name")
                val storeName: String?,
                @SerializedName("store_address")
                val storeAddress: String?,
                @SerializedName("bio")
                val bio: String?
            )
        }
    }
}
