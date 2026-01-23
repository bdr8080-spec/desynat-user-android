package com.desynatuser.ui.affiliateUser.auth.response


import com.google.gson.annotations.SerializedName

data class AddAffiliateUserAddressResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("address1")
        val address1: String,
        @SerializedName("address2")
        val address2: String,
        @SerializedName("affiliate_id")
        val affiliateId: String,
        @SerializedName("city")
        val city: String,
        @SerializedName("country")
        val country: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("let")
        val let: String,
        @SerializedName("locations")
        val locations: String,
        @SerializedName("long")
        val long: String,
        @SerializedName("pincode")
        val pincode: String,
        @SerializedName("state")
        val state: String,
        @SerializedName("town")
        val town: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}