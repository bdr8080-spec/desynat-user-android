package com.desynatuser.ui.home.fragment.response


import com.google.gson.annotations.SerializedName

data class GetAllSuggestStoreResponse(
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