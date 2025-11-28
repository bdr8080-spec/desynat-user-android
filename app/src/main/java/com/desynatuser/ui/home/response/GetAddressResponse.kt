package com.desynatuser.ui.home.response


import com.google.gson.annotations.SerializedName

data class GetAddressResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("address")
        val address: String,
        @SerializedName("city")
        val city: String,
        @SerializedName("Country_name")
        val country: String?=null,
        @SerializedName("country_code")
        val countryCode: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("house_no")
        val houseNo: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("landmark")
        val landmark: String,
        @SerializedName("latitude")
        val latitude: Double,
        @SerializedName("longitude")
        val longitude: Double,
        @SerializedName("mobile")
        val mobile: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("postal_code")
        val postalCode: String,
        @SerializedName("save_address_as")
        val saveAddressAs: Int,
        @SerializedName("state")
        val state: String,
        @SerializedName("status")
        val status: Int,
        @SerializedName("street_no")
        val streetNo: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("set_as_default")
        var setAsDefault: String
    )
}