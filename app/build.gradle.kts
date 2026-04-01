plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-android")
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    alias(libs.plugins.hilt.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.desynatuser"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.desynatuser"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    configurations.all {
        resolutionStrategy {
            // Force a single guava version
            force("com.google.guava:guava:30.1.1-android")

            // Exclude listenablefuture completely
            exclude(group = "com.google.guava", module = "listenablefuture")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // Logging interceptor
    implementation(libs.okhttp)
//    coil
    implementation("io.coil-kt:coil:2.7.0")
    implementation("io.coil-kt:coil-svg:2.7.0")

//    for opt
    implementation(libs.ccp)

    implementation("io.github.chaosleung:pinview:1.4.4")


    //map
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.libraries.places:places:3.5.0")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.tbuonomo:dotsindicator:5.1.0")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("com.yazantarifi:slider:1.0.0")

    implementation ("com.google.android.material:material:1.4.0")
    implementation ("com.myfatoorah:myfatoorah:3.0.4")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    implementation ("org.greenrobot:eventbus:3.3.1")
    implementation ("com.google.android.gms:play-services-wallet:19.4.0")
    implementation ("com.airbnb.android:lottie:6.7.1")

    implementation("com.google.api-client:google-api-client-android:1.35.2") {
        exclude(module = "httpclient")
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
    implementation("com.google.http-client:google-http-client-gson:1.42.3") {
        exclude(module = "httpclient")
        exclude(group = "com.google.guava", module = "listenablefuture")
    }

    implementation("io.getstream:photoview:1.0.2")

    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation ("com.google.firebase:firebase-database-ktx")
    implementation ("com.google.firebase:firebase-auth-ktx")

    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.camera:camera-core:1.3.2")
    implementation("androidx.camera:camera-camera2:1.3.2")
    implementation("androidx.camera:camera-lifecycle:1.3.2")
    implementation("androidx.camera:camera-view:1.3.2")

    implementation("io.branch.sdk.android:library:5.+")

    implementation("com.google.firebase:firebase-analytics")

    implementation ("com.google.firebase:firebase-messaging:25.0.1")
    implementation ("org.greenrobot:eventbus:3.3.1")
    implementation("me.leolin:ShortcutBadger:1.1.22@aar")
    implementation("io.agora.rtc:full-sdk:4.2.3")
}