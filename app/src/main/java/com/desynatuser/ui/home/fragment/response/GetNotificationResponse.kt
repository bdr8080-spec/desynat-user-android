package com.desynatuser.ui.home.fragment.response


import com.google.gson.annotations.SerializedName

data class GetNotificationResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("current_page")
        val currentPage: Int,
        @SerializedName("data")
        val `data`: List<Data>,
        @SerializedName("first_page_url")
        val firstPageUrl: String,
        @SerializedName("from")
        val from: Int,
        @SerializedName("last_page")
        val lastPage: Int,
        @SerializedName("last_page_url")
        val lastPageUrl: String,
        @SerializedName("links")
        val links: List<Link>,
        @SerializedName("next_page_url")
        val nextPageUrl: Any,
        @SerializedName("path")
        val path: String,
        @SerializedName("per_page")
        val perPage: Int,
        @SerializedName("prev_page_url")
        val prevPageUrl: Any,
        @SerializedName("to")
        val to: Int,
        @SerializedName("total")
        val total: Int
    ) {
        data class Data(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("description_arabic")
            val descriptionArabic: String,
            @SerializedName("description_english")
            val descriptionEnglish: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_read")
            var isRead: Int,
            @SerializedName("notification_type")
            val notificationType: Any,
            @SerializedName("receiver_id")
            val receiverId: Int,
            @SerializedName("receiver_type")
            val receiverType: Int,
            @SerializedName("sender_id")
            val senderId: Int,
            @SerializedName("sender_type")
            val senderType: Int,
            @SerializedName("title_arabic")
            val titleArabic: String,
            @SerializedName("title_english")
            val titleEnglish: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class Link(
            @SerializedName("active")
            val active: Boolean,
            @SerializedName("label")
            val label: String,
            @SerializedName("url")
            val url: String
        )
    }
}