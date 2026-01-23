package com.desynatuser.ui.affiliateUser.auth.response


import com.google.gson.annotations.SerializedName

data class AffiliateUserAddBankDetailRequest(
    @SerializedName("account_holder_name")
    val accountHolderName: String,
    @SerializedName("account_no")
    val accountNo: String,
    @SerializedName("bank_name")
    val bankName: String,
    @SerializedName("confirm_account_no")
    val confirmAccountNo: String,
    @SerializedName("iban")
    val iban: String,
    @SerializedName("swift")
    val swift: String
)