package com.desynatuser.ui.home.myOrder.response


import com.google.gson.annotations.SerializedName

data class GetInvoiceResponse(
    @SerializedName("data")
    val `data`: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)