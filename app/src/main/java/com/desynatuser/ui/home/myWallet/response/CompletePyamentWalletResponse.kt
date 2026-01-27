package com.desynatuser.ui.home.myWallet.response


import com.google.gson.annotations.SerializedName

data class CompletePyamentWalletResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("addedby")
        val addedby: String,
        @SerializedName("amount")
        val amount: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("payment_method")
        val paymentMethod: String,
        @SerializedName("payment_status")
        val paymentStatus: String,
        @SerializedName("transaction_id")
        val transactionId: Any,
        @SerializedName("type")
        val type: Int,
        @SerializedName("unique_id")
        val uniqueId: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int
    )
}