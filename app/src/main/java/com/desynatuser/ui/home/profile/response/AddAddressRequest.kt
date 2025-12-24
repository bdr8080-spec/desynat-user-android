package com.desynatuser.ui.home.profile.response


import com.google.gson.annotations.SerializedName

data class AddAddressRequest(
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("house_no")
    val houseNo: String,
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
    val saveAddressAs: Int,// 0 for office 1 for home
    @SerializedName("state")
    val state: String,
   /* @SerializedName("street_no")
    val streetNo: String,*/
    @SerializedName("Country_name")
    val country: String,
    @SerializedName("id")
    val id: String?=null
)