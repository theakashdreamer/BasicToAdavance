package com.skysoftsolution.thingisbeing.utility

import android.content.Context
import android.util.Base64
import java.io.UnsupportedEncodingException

class GlobalClass {
    companion object {
        fun getAppID(context: Context): String {
            return "1"
        }

        fun getVersionCode(context: Context): String {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return pInfo.versionName.toString() + ""
        }

        fun convertStringIntoEncodedBase64String(ActualString: String): String? {
            var base64String: String? = ""
            val descriptionInByte: ByteArray
            try {
                descriptionInByte = ActualString.toByteArray(charset("UTF-8"))
                base64String = Base64.encodeToString(descriptionInByte, Base64.DEFAULT)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return base64String
        }
    }
}