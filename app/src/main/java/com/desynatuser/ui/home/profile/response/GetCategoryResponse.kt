package com.desynatuser.ui.home.profile.response


import com.google.gson.annotations.SerializedName

data class GetCategoryResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("color_code")
        val colorCode: String?=null,
        @SerializedName("created_at")
        val createdAt: String?=null,
        @SerializedName("description")
        val description: String?=null,
        @SerializedName("id")
        val id: Int,
        @SerializedName("image")
        val image: String?=null,
        @SerializedName("name")
        val name: String?=null,
        @SerializedName("name_ar")
        val nameAr: String?=null,
        @SerializedName("status")
        val status: String?=null,
        @SerializedName("updated_at")
        val updatedAt: String?=null
    )
}