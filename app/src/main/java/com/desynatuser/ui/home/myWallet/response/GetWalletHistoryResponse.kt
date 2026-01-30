package com.desynatuser.ui.home.myWallet.response


import com.google.gson.annotations.SerializedName

data class GetWalletHistoryResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("history")
        val history: List<History>,
        @SerializedName("wallet_balance")
        val walletBalance: Double
    ) {
        data class History(
            @SerializedName("addedby")
            val addedby: String,
            @SerializedName("amount")
            val amount: Double,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("payment_method")
            val paymentMethod: String,
            @SerializedName("payment_status")
            val paymentStatus: String,
            @SerializedName("transaction_id")
            val transactionId: String,
            @SerializedName("order_id")
            val orderId: String,
            @SerializedName("type")
            val type: String,
            @SerializedName("unique_id")
            val uniqueId: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: String,
            @SerializedName("giftCardNo")
            val giftCardNo: String
        )
    }
}