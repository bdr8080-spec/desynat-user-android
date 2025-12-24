package com.desynatuser.ui.home.profile.response

import com.google.gson.annotations.SerializedName

data class GetPromoCodeResponse(
    @SerializedName("data")
    val `data`: ArrayList<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
        data class Data(
            @SerializedName("applicable_for")
            val applicableFor: Int,
            @SerializedName("country_id")
            val countryId: Int,
            @SerializedName("country_info")
            val countryInfo: CountryInfo,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("description_arabic")
            val descriptionArabic: String,
            @SerializedName("description_english")
            val descriptionEnglish: String,
            @SerializedName("discount_percent")
            val discountPercent: String,
            @SerializedName("end_date")
            val endDate: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("min_order_value")
            val minOrderValue: String,
            @SerializedName("name_arabic")
            val nameArabic: String,
            @SerializedName("name_english")
            val nameEnglish: String,
            @SerializedName("start_date")
            val startDate: String,
            @SerializedName("status")
            val status: Int,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("upto_discount")
            val uptoDiscount: String
        ) {
            data class CountryInfo(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("currency_code")
                val currencyCode: String,
                @SerializedName("currency_name")
                val currencyName: String,
                @SerializedName("currency_symbol")
                val currencySymbol: String,
                @SerializedName("dial_code")
                val dialCode: String,
                @SerializedName("exchange_rate_to_kwd")
                val exchangeRateToKwd: String,
                @SerializedName("flag")
                val flag: String,
                @SerializedName("flag1")
                val flag1: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("iso_code")
                val isoCode: String,
                @SerializedName("name")
                val name: String,
                @SerializedName("updated_at")
                val updatedAt: String
            )
        }
}