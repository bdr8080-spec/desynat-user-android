package com.desynatuser.ui.home.view_all_product.response

import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.google.gson.annotations.SerializedName

data class ViewAllProductResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: Data
) {
    data class Data(
        @SerializedName("All_Product")
        val allProduct: GetSingleStoreResponse.Data.AllProduct
    )
}
