package com.desynatuser.ui.response


import com.google.gson.annotations.SerializedName

data class GetHomeScreenAllBannerResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("Admin_verify")
        val adminVerify: String?=null,
        @SerializedName("banner_position")
        val bannerPosition: String?=null,
        @SerializedName("cancelled_reason")
        val cancelledReason: String?=null,
        @SerializedName("category")
        val category: String?=null,
        @SerializedName("created_at")
        val createdAt: String?=null,
        @SerializedName("end_date")
        val endDate: String?=null,
        @SerializedName("id")
        val id: String?=null,
        @SerializedName("order_id")
        val orderId: String?=null,
        @SerializedName("payment_status")
        val paymentStatus: String?=null,
        @SerializedName("price")
        val price: String?=null,
        @SerializedName("seller_id")
        val sellerId: String?=null,
        @SerializedName("start_date")
        val startDate: String?=null,
        @SerializedName("status")
        val status: String?=null,
        @SerializedName("total_days")
        val totalDays: String?=null,
        @SerializedName("transaction_id")
        val transactionId: String?=null,
        @SerializedName("updated_at")
        val updatedAt: String?=null,
        @SerializedName("upload_banner")
        val uploadBanner: String?=null
    ) {
        data class CategoryDetails(
            @SerializedName("color_code")
            val colorCode: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("description")
            val description: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("image")
            val image: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("status")
            val status: Int,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }
}