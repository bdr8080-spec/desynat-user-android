package com.desynatuser.ui.home.myOrder.response


import com.google.gson.annotations.SerializedName

data class GetReasonListResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String?=null,
        @SerializedName("id")
        val id: Int=0,
        @SerializedName("reason_arabic")
        val reasonArabic: String?=null,
        @SerializedName("reason_english")
        val reasonEnglish: String?=null,
        @SerializedName("status")
        val status: Int=0,
        @SerializedName("type")
        val type: Int=0,
        @SerializedName("updated_at")
        val updatedAt: String?=null
    )
}