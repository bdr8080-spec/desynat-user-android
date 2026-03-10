package com.desynatuser

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.myfatoorah.sdk.enums.MFCountry
import com.myfatoorah.sdk.enums.MFEnvironment
import com.myfatoorah.sdk.views.MFSDK
import dagger.hilt.android.HiltAndroidApp
import io.branch.referral.Branch

@HiltAndroidApp
class CoreApplication : Application(){
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

}
}