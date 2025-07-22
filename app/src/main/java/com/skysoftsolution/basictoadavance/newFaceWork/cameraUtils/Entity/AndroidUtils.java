package com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.Entity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AndroidUtils {

    public static int notificationCounter = 0;

    public static boolean checkYourMobileDataConnection(Context context) {

        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        boolean iswifiConn = networkInfo.isConnected();

        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean ismobileConn = false;
        if (networkInfo != null) {
            ismobileConn = networkInfo.isConnected();
        }


        boolean returnString = false;
        if (iswifiConn || ismobileConn) {

            Log.d("DEBUG", "DATA connected: ");
            returnString = true;

        }

        return returnString;

    }

    public static String checkYourDataConnectionType(Context context) {

        String dataConnType = "";
        TelephonyManager tlManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = tlManager.getNetworkType();

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                dataConnType = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_UMTS:
                dataConnType = "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                dataConnType = "4G";
                break;

            default:
                dataConnType = "unknown";
                break;
        }

        return dataConnType;

    }

    // for mobile data coonection check
    public static boolean isOnlyMobileDataConnected(Context context) {

        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean ismobileConn = networkInfo.isConnected();

        boolean isMobileDataConnected = false;
        if (ismobileConn) {

            Log.d("DEBUG", "DATA connected: ");
            isMobileDataConnected = true;

        }

        return isMobileDataConnected;

    }

    // for mobile data coonection check End

}