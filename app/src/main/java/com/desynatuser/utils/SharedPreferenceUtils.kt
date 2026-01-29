package com.desynatuser.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.desynatuser.R
import com.desynatuser.adapter.SavedAddressAdapter
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserLoginResponse
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.ui.home.profile.response.GetAddressResponse
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreference(context: Context) {
    val preference: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = preference.edit()

    companion object {
        var instance: SharedPreference? = null
        fun get(ctx: Context): SharedPreference {
            if (instance == null) {
                instance = SharedPreference(ctx)
            }
            return instance!!
        }
    }

    operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is Int -> edit { it.putInt(key, value) }
            is String? -> edit { it.putString(key, value) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is Long -> edit { it.putLong(key, value) }
            else -> Log.e("TAG", "Setting shared pref failed for key: $key and value: $value ")
        }
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    inline operator fun <reified T : Any> SharedPreferences.get(
        key: String,
        defaultValue: T? = null
    ): T? {
        return when (T::class) {
            String::class -> getString(key, defaultValue as? String) as T?
            Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
            Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    var language: String
        get() = preference["language", "en"]!!
        set(value) = preference.set("language", value)

    var isTutorial: Boolean
        get() = preference["tutorial", false]!!
        set(value) = preference.set("tutorial", value)

    var userType: Int
        get() = preference["userType", 1]!!
        set(value) = preference.set("userType", value)

    var accessToken: String
        get() = preference["accessToken", ""]!!
        set(value) = preference.set("accessToken", value)

    var affiliateAccessToken: String
        get() = preference["affiliateAccessToken", ""]!!
        set(value) = preference.set("affiliateAccessToken", value)

    var deviceToken: String
        get() = preference["deviceToken", ""]!!
        set(value) = preference.set("deviceToken", value)


    fun saveUser(user: LoginResponse.Data) {
        val json = Gson().toJson(user)
        preference.set("userData", json)
    }

    fun getUser(): LoginResponse.Data? {
        val json = preference["userData", ""] ?: return null
        return try {
            Gson().fromJson(json, LoginResponse.Data::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearUser() {
        preference.set("userData", "")
    }

    fun saveAffiliateUser(user: AffiliateUserLoginResponse.Data) {
        val json = Gson().toJson(user)
        preference.set("affiliateUserData", json)
    }

    fun getAffiliateUser(): AffiliateUserLoginResponse.Data? {
        val json = preference["affiliateUserData", ""] ?: return null
        return try {
            Gson().fromJson(json, AffiliateUserLoginResponse.Data::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearAffiliateUser() {
        preference.set("affiliateUserData", "")
    }


    fun saveCountryList(countryList: List<GetCountryResponse.Data>) {
        val json = Gson().toJson(countryList)
        preference.set("countryList", json)
    }

    fun getCountryList(): List<GetCountryResponse.Data>? {
        val json = preference["countryList", ""] ?: return null
        return try {
            val type = object : TypeToken<List<GetCountryResponse.Data>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun saveSelectedCountryList(countryList: List<GetCountryResponse.Data>) {
        val json = Gson().toJson(countryList)
        preference.set("selectedCountryList", json)
    }

    fun getSelectedCountryList(): List<GetCountryResponse.Data>? {
        val json = preference["selectedCountryList", ""] ?: return null
        return try {
            val type = object : TypeToken<List<GetCountryResponse.Data>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun saveCategoryLIst(countryList: List<GetCategoryResponse.Data>) {
        val json = Gson().toJson(countryList)
        preference.set("categoryList", json)
    }

    fun getCategoryList(): List<GetCategoryResponse.Data>? {
        val json = preference["categoryList", ""] ?: return null
        return try {
            val type = object : TypeToken<List<GetCategoryResponse.Data>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun saveDefaultAddress(address: GetAddressResponse.Data) {
        val json = Gson().toJson(address)
        preference.set("defaultAddress", json)
    }

    fun getDefaultAddress(): GetAddressResponse.Data? {
        val json = preference["defaultAddress", ""] ?: return null
        return try {
            Gson().fromJson(json, GetAddressResponse.Data::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearAddress() {
        preference.set("defaultAddress", "")
    }

}