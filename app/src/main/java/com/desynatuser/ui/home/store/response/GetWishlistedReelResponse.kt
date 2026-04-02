package com.desynatuser.ui.home.store.response


import com.google.gson.annotations.SerializedName

data class GetWishlistedReelResponse(
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
        @SerializedName("reel_details")
        val reelDetails: com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse.Data,
        @SerializedName("reel_id")
        val reelId: Int,
        @SerializedName("status")
        val status: Int,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int
    ) {
        data class ReelDetails(
            @SerializedName("caption")
            val caption: Any,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("file")
            val `file`: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("status")
            val status: Int,
            @SerializedName("type")
            val type: Int,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }
}