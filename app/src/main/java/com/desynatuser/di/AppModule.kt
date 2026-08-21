package com.desynatuser.di

import android.content.Context
import com.desynatuser.network.ApiServices
import com.desynatuser.utils.Constants
import com.desynatuser.utils.SharedPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor { chain ->
                val request = chain.request()
                val requestBuilder = request.newBuilder()

                if (!request.url.host.contains("googleapis.com")) {
                    var token = ""
                    if (SharedPreference.get(context).userType == 2) {
                        if (SharedPreference.get(context).affiliateAccessToken != "") {
                            token = SharedPreference.get(context).affiliateAccessToken
                        } else {
                            token = SharedPreference.get(context).accessToken
                        }
                    } else {
                        token = SharedPreference.get(context).accessToken
                    }

                    if (token.isNotEmpty()) {
                        requestBuilder.addHeader("Authorization", "Bearer $token")
                        requestBuilder.addHeader("Accept-Language", "${Constants.language}")
                    }
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }


    @Provides
    @Singleton
    fun provideApi(retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient): ApiServices {
        return retrofitBuilder.client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices::class.java)
    }

}