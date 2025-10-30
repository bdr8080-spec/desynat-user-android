package com.desynatuser.utils

object Constants {

    const val BASE_URL = "http://157.241.119.72"

    var language=""
    const val ENGLISH = "en"
    const val ARABIC = "ar"
    var isGuest = false

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

}