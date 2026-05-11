package com.desynatuser.ui.home.store.response


import com.google.gson.annotations.SerializedName

data class GetSizeListResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("name_ar")
        val nameAr: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("isSelected")
        var isSelected: Boolean = false
    )
}