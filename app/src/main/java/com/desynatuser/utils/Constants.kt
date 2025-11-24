package com.desynatuser.utils

object Constants {

    const val BASE_URL = "http://157.241.119.72"

    var language="en"
    const val ENGLISH = "en"
    const val ARABIC = "ar"
    var isGuest = false
    var Category = "Women"

    var isLogout=false
    var location=""
    var latitude=0.0
    var longitude=0.0
    var DEVICE_TYPE=1

    const val UPLOAD = "/api/upload"
    const val REGISTER = "/api/register"
    const val LOGIN = "/api/login"
    const val OTP_VERIFY = "/api/verify_otp"
    const val RESEND_OTP = "/api/resend_otp"
    const val FORGOT_PASSWORD = "/api/forgot_password"
    const val CREATE_PASSWORD = "/api/create_password"
    const val LOGIN_VIA_EMAIL_OR_MOBILE = "/api/login_by_mobile_or_email"
    const val DASHBOARD = "/api/user/user-dashboard"
    const val LOGOUT = "/api/user/logout"
    const val GET_COUNTRY = "/api/all_country"
    const val CHANGE_PASSWORD = "/api/user/change-password"
    const val GET_CATEGORY = "/api/user/category"
    const val GET_SUB_CATEGORY = "/api/user/sub-category"
    const val UPDATE_PROFILE = "/api/user/update-profile"
    const val CHANGE_NUMBER = "/api/user/change-mobile-number"
    const val USER_OTP = "/api/user/verify-otp"

    const val STORE_LIST = "/api/user/category-Wise-Store-List"
    const val SINGLE_STORE = "/api/user/single-store-details"
    const val ADD_TO_CART = "/api/user/product-add-to-cart"
    const val ADD_TO_CART_QUANTITY = "/api/user/addtocart-increment-decrement"
    const val VIEW_CART_DETAIL = "/api/user/view_cart_details"
    const val ADD_TO_WISHLIST = "/api/user/product-add-to-wishlist"


    const val SAVE_ADDRESS = "/api/user/save-address"
    const val GET_ADDRESS = "/api/user/view-address"
    const val EDIT_ADDRESS = "/api/user/update-address"
    const val DELETE_ADDRESS = "/api/user/delete-address"


}