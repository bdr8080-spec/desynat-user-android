package com.desynatuser

import android.app.Application
import android.app.NotificationManager
import android.util.Log
import com.desynatuser.utils.SharedPreference
import com.google.android.libraries.places.api.Places
import com.google.firebase.messaging.FirebaseMessaging
import com.myfatoorah.sdk.enums.MFCountry
import com.myfatoorah.sdk.enums.MFEnvironment
import com.myfatoorah.sdk.views.MFSDK
import dagger.hilt.android.HiltAndroidApp
import io.branch.referral.Branch

@HiltAndroidApp
class CoreApplication : Application(){
    companion object {
        var notificationManager: NotificationManager? = null
    }
override fun onCreate() {
    super.onCreate()

    Branch.enableLogging()
    Branch.getAutoInstance(this)

    if (!Places.isInitialized()) {
        Places.initialize(applicationContext, getString(R.string.map_key))
    }
/*
    MFSDK.init(
        getString(R.string.my_fatoorah_token),
        MFCountry.UNITED_ARAB_EMIRATES_UAE,
        MFEnvironment.LIVE
    )*/

    MFSDK.init(
        getString(R.string.fatoorah),
        MFCountry.KUWAIT,
        MFEnvironment.TEST
    )

    notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.e("FCM_ERROR", "Token failed", task.exception)
            return@addOnCompleteListener
        }

        val token = task.result
        Log.d("FCM_TOKEN", token)
        SharedPreference.get(this).deviceToken = token
    }

}
}