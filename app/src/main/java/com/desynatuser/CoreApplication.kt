package com.desynatuser

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.myfatoorah.sdk.enums.MFCountry
import com.myfatoorah.sdk.enums.MFEnvironment
import com.myfatoorah.sdk.views.MFSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoreApplication : Application(){
override fun onCreate() {
    super.onCreate()

    if (!Places.isInitialized()) {
        Places.initialize(applicationContext, getString(R.string.map_key))
    }

    MFSDK.init(
        getString(R.string.my_fatoorah_token_test),
        MFCountry.KUWAIT,
        MFEnvironment.TEST
    )

}
}