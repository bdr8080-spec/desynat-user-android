package com.desynatuser.ui.affiliateUser.home.response


import com.google.gson.annotations.SerializedName

data class GetHomeDashboardResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("total_cancelled")
        val totalCancelled: Int,
        @SerializedName("total_completed")
        val totalCompleted: Int,
        @SerializedName("total_earning")
        val totalEarning: String,
        @SerializedName("total_ongoing")
        val totalOngoing: Int,
        @SerializedName("total_orders")
        val totalOrders: Int,
        @SerializedName("total_products")
        val totalProducts: Int,
        @SerializedName("total_returned")
        val totalReturned: Int,
        @SerializedName("total_revenue")
        val totalRevenue: String
    )
}