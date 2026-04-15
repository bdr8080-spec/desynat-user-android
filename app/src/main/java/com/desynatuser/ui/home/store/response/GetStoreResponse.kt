package com.desynatuser.ui.home.store.response


import com.google.gson.annotations.SerializedName

data class GetStoreResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
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
        @SerializedName("seller_information")
        val sellerInformation: SellerInformation,
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
        val updatedAt: String,
        @SerializedName("average_store_rating")
        val averageStoreRating: String

    ) {
        data class SellerInformation(
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
            val deviceToken: String,
            @SerializedName("device_type")
            val deviceType: String,
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
            val rejectReason: String,
            @SerializedName("store_information")
            val storeInformation: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }
}