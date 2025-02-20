package com.skysoftsolution.thingisbeing.utility
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
class AppPreferences(private val context: Context) {
    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setSavedPrivacyPolicy() {
        prefs.edit().putBoolean(PreferenceKeys.PRIVACY_POLICY, true).apply()
    }

    fun getSavedPrivacyPolicy(): Boolean {
        return prefs.getBoolean(PreferenceKeys.PRIVACY_POLICY, false)
    }


    fun setOtpDataModel(otp: String) {
        prefs.edit().putString(PreferenceKeys.OTP_Model, otp).apply()
    }

    fun getOtpDataModel(): String {
        return prefs.getString(PreferenceKeys.OTP_Model, "")!!
    }

    fun setLoginDataScareen() {
        prefs.edit().putBoolean(PreferenceKeys.LOGIN_SCAREEN, true).apply()
    }

    fun getLoginDataScareen(): Boolean {
        return prefs.getBoolean(PreferenceKeys.LOGIN_SCAREEN, false)
    }

    fun setUrl(url: String) {
        prefs.edit().putString(PreferenceKeys.URL1, url).apply()
    }

    fun getUrl(): String {
        return prefs.getString(PreferenceKeys.URL1, "")!!
    }

    fun setPinGenerateDataDetails(strOtp: String) {
        prefs.edit().putString(PreferenceKeys.PIN_GENERATE, strOtp).apply()
    }

    fun getPinGenerateDataDetails(): String {
        return prefs.getString(PreferenceKeys.PIN_GENERATE, "")!!
    }

    fun setPinGenerateForLoginTrueAndFalse(b: Boolean) {
        prefs.edit().putBoolean(PreferenceKeys.PIN_GENERATE_BOOLEAN, true).apply()
    }

    fun getPinGenerateForLoginTrueAndFalse(): Boolean {
        return prefs.getBoolean(PreferenceKeys.PIN_GENERATE_BOOLEAN, false)
    }

    fun setLoginUserData(strOtp: String) {
        prefs.edit().putString(PreferenceKeys.USER_DETAILS, strOtp).apply()
    }

    fun getLoginUserData(): String {
        return prefs.getString(PreferenceKeys.USER_DETAILS, "")!!
    }

    fun clearPreferencesData() {
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun setOtpScreenStatus(values: Boolean) {
        prefs.edit().putBoolean(PreferenceKeys.OTP_SCREEN_STATUS, values).apply()
    }

    fun getOtpScreenStatus(): Boolean {
        return prefs.getBoolean(PreferenceKeys.OTP_SCREEN_STATUS, false)
    }

    fun setpublicData(publicuser: String?) {
        prefs.edit().putString(PreferenceKeys.PUBLICDATA, publicuser).apply()
    }

    fun getpublicData(): String {
        return prefs.getString(PreferenceKeys.PUBLICDATA, "")!!
    }
}