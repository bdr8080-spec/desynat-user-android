package com.desynatuser.ui.affiliateUser.home.payment.response


import com.google.gson.annotations.SerializedName

data class GetWithdrawalRequestResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("total_withdrawals")
        val totalWithdrawals: String,
        @SerializedName("withdraw_requests")
        val withdrawRequests: List<WithdrawRequest>
    ) {
        data class WithdrawRequest(
            @SerializedName("admin_verify")
            val adminVerify: Int,
            @SerializedName("amount")
            val amount: String,
            @SerializedName("bank_details")
            val bankDetails: BankDetails,
            @SerializedName("bankdetails_id")
            val bankdetailsId: Int,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("reject_reason")
            val rejectReason: String,
            @SerializedName("request_id")
            val requestId: String,
            @SerializedName("transaction_id")
            val transactionId: String,
            @SerializedName("type")
            val type: Int,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: Int
        ) {
            data class BankDetails(
                @SerializedName("account_holder_name")
                val accountHolderName: String,
                @SerializedName("account_no")
                val accountNo: String,
                @SerializedName("account_type")
                val accountType: String,
                @SerializedName("bank_name")
                val bankName: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("default_account")
                val defaultAccount: String,
                @SerializedName("iban")
                val iban: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("swift")
                val swift: String,
                @SerializedName("type")
                val type: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("upload_proof")
                val uploadProof: String,
                @SerializedName("user_id")
                val userId: String
            )
        }
    }
}