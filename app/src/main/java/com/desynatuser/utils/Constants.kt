package com.desynatuser.utils

object Constants {

    const val BASE_URL = "http://157.241.119.72"

    var MERCHANT_ID = "BCR2DN5TRCW73QI6"
    var MERCHANT_NAME = "Desynat"

    var language="en"
    var currentScreen = ""
    const val ENGLISH = "en"
    const val ARABIC = "ar"
    var isGuest = false
    var Category = "Women"

    var isLogout=false
    var location=""
    var latitude=0.0
    var longitude=0.0
    var cartItem=0
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
    const val GET_PRODUCT_DETAIL = "api/user/single-product-details"
    const val ADD_TO_CART = "/api/user/product-add-to-cart"
    const val ADD_TO_CART_QUANTITY = "/api/user/addtocart-increment-decrement"
    const val VIEW_CART_DETAIL = "/api/user/view_cart_details"
    const val ADD_TO_WISHLIST = "/api/user/product-add-to-wishlist"
    const val GET_STORE_SORT_LIST = "/api/store_filter"
    const val GET_WISHLIST = "/api/user/get_wishlist_products"
    const val GET_SEARCH = "/api/store_filter"
    const val GET_COLOR_LIST = "/api/all-color"
    const val GET_BRAND_LIST = "/api/all-brand"
    const val GET_SIZE_LIST = "/api/all-size"

    const val SAVE_ADDRESS = "/api/user/save-address"
    const val GET_ADDRESS = "/api/user/view-address"
    const val EDIT_ADDRESS = "/api/user/update-address"
    const val DELETE_ADDRESS = "/api/user/delete-address"
    const val DEFAULt_ADDRESS = "/api/user/set_default_address"

    const val GIFT_CARD_LIST = "/api/user/view-gift-cards"
    const val GIFT_CARD_OCCASION = "/api/user/gift-card-occasion-list"
    const val BUY_GIFT_CARD = "/api/user/add-gift-card"
    const val BUY_GIFT_CARD_PAYMENT = "/api/user/giftcard-complete-Payment"
    const val APPLY_GIFT_CARD = "/api/user/apply-gift-card"
    const val PROMO_CODE_LIST = "/api/user/view-promocode-list"
    const val APPLY_PROMO_CODE = "/api/user/Apply-promocode"
    const val PLACE_ORDER = "/api/user/create-order"
    const val COMPLETE_ORDER_PAYMENT = "/api/user/product-order-complete-payment"
    const val GET_ALL_ORDER = "/api/user/All-order"
    const val GET_ORDER_SUMMARY = "/api/user/single-order-summary"
    const val GET_REASON = "/api/user/get-reject-reason"
    const val CANCEL_ORDER = "/api/user/cancelled-single-product"
    const val RETURN_EXCHANGE_ORDER = "/api/user/return-exchange-product-after-delivered"
    const val ORDER_RATING = "/api/user/product-rating"
    const val RECENT_SEARCH_LIST = "/api/user/get-recent-search-data"
    const val LATEST_SEARCH_LIST = "api/user/get-latest-search-data"
    const val RECOMMENDED_SEARCH_LIST = "/api/user/get-recommnded-search-data"
    const val HOME_PAGE_SEARCH = "/api/user/home-page-search"

    const val WALLET_BALANCE = "/api/user/waller-balance"
    const val INVOICE = "/api/user/get-Invoice"

    const val CREATE_CUSTOM_ORDER = "/api/user/create-Custom-order"
    const val SEND_CUSTOM_ORDER_REQUEST = "/api/user/send-custom-order-request"
    const val APPLY_CUSTOM_ORDER_GIFT_CARD = "/api/user/custom-create-order"
    const val CUSTOM_ORDER_COMPLETE_PAYMENT = "/api/user/product-order-complete-payment"

    const val USER_ALL_CHAT = "/api/user/user-all-chats"
    const val USER_SINGLE_CHAT = "/api/user/user-Single-chats"
    const val SEND_MESSAGE = "/api/user/send-message-in-chats"
    const val CHAT_WITH_SELLER = "/api/user/user-normal-chat-with-seller"
    const val HOME_SCREEN_BANNER = "/api/user/popUp-Home-screen-Banner"
    const val ACCEPT_REJECT_ORDER_REQUEST = "/api/user/custom-accept-reject-order-request"
    const val HOME_SCREEN_ALL_BANNER = "/api/user/Home-screen-Banner-catgory-banner"

    const val ADD_MONEY_IN_WALLET = "/api/user/Add-money-in-wallet"
    const val COMPLETE_PAYMENT_ADD_MONEY_WALLET = "/api/user/complete-add-money-in-wallet"

   //affiliate User
    const val CREATE_AFFILIATE_USER = "/api/user/create-a-affilated-account"
    const val VERIFY_OTP_AFFILIATE_USER = "/api/user/affilated-verify-otp"
    const val ADD_ADDRESS_AFFILIATE_USER = "/api/user/affilated-add-address"
    const val ADD_BANK_DETAIL_AFFILIATE_USER = "/api/user/affilated-add-Bank-details"
    const val LOGIN_AFFILIATE_USER = "/api/affiliate/affiliated-login"
    const val FORGOT_PASSWORD_AFFILIATE_USER = "/api/affiliate/forgot_password"
    const val RESET_OTP_AFFILIATE_USER = "/api/affiliate/resend_otp"
    const val CREATE_PASSWORD_AFFILIATE_USER = "/api/affiliate/create_password"

}