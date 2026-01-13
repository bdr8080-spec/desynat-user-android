package com.desynatuser.ui.response


import com.google.gson.annotations.SerializedName

data class GetHomeScreenPopUpBanner(
    @SerializedName("data")
    val `data`: ArrayList<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("country_id")
        val countryId: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("end_date")
        val endDate: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("image")
        val image: String,
        @SerializedName("start_date")
        val startDate: String,
        @SerializedName("status")
        val status: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}