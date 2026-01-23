package com.desynatuser.ui.affiliateUser.auth.response


import com.google.gson.annotations.SerializedName

data class AddAffiliateUserBankDetail(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("account_holder_name")
        val accountHolderName: String,
        @SerializedName("account_no")
        val accountNo: String,
        @SerializedName("bank_name")
        val bankName: String,
        @SerializedName("created_at")
        val createdAt: String,
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
        @SerializedName("user_id")
        val userId: Int
    )
}