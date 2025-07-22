package com.skysoftsolution.basictoadavance.sharedpreference;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceData {

    public void SetAPIURL(Context context, String APIURL) {
        SharedPreferences shreSharedPreferences = context.getSharedPreferences("ApiUPL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shreSharedPreferences.edit();
        editor.putString("URL", APIURL);
        editor.commit();
    }

    public String getAPIURL(Context context) {
        SharedPreferences sharedPreferenceForURL = context.getSharedPreferences("ApiUPL", MODE_PRIVATE);
        String url = sharedPreferenceForURL.getString("URL", null);
        return url;
    }


    public void SetDeviceId(Context context, String DeviceId) {
        SharedPreferences shreSharedPreferences = context.getSharedPreferences("DeviceID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shreSharedPreferences.edit();
        editor.putString("DeviceID", DeviceId);
        editor.commit();
    }

    public String getDeviceId(Context context) {
        SharedPreferences shreSharedPreferences = context.getSharedPreferences("DeviceID", Context.MODE_PRIVATE);
        String url = shreSharedPreferences.getString("DeviceID", null);
        return url;
    }


    public void SetMobilePasswordDesignation(Context context, String UserMobile, String Pin, String Designation) {
        SharedPreferences shreSharedPreferences = context.getSharedPreferences("UserMobilePinDesignation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shreSharedPreferences.edit();
        editor.putString("UserMobile", UserMobile);
        editor.putString("Pin", Pin);
        editor.putString("Designation", Designation);
        editor.commit();
    }

    public void SetToken(Context context, String Token) {
        SharedPreferences shreSharedPreferences = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shreSharedPreferences.edit();
        editor.putString("token", Token);
        editor.commit();
    }

    public String getToken(Context context) {
        SharedPreferences shreSharedPreferences = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
        String token = shreSharedPreferences.getString("token", null);
        return token;
    }

    public void SetLoginstatus(Context context, String loginstatus) {
        SharedPreferences shreSharedPreferences = context.getSharedPreferences("LoginStatus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shreSharedPreferences.edit();
        editor.putString("loginstatus", loginstatus);
        editor.commit();
    }

    public String getLoginstatus(Context context) {
        SharedPreferences shreSharedPreferences = context.getSharedPreferences("LoginStatus", Context.MODE_PRIVATE);
        String login = shreSharedPreferences.getString("loginstatus", "");
        return login;
    }

    public void SetDateTime(Context context, String NtpServerTime, String LocalTime) {
        SharedPreferences shreSharedPreferences = context.getSharedPreferences("LoginStatus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shreSharedPreferences.edit();
        editor.putString("NtpServerTime", NtpServerTime);
        editor.putString("LocalTime", LocalTime);
        editor.commit();
    }

    public void setlocationprofile(Context context) {
        SharedPreferences shreSharedPreferences = context.getSharedPreferences("LocationProfile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shreSharedPreferences.edit();
        editor.putString("LocationProfile", "true");
        editor.commit();
    }

    public String getLoginprofile(Context context) {
        SharedPreferences shreSharedPreferences = context.getSharedPreferences("LocationProfile", Context.MODE_PRIVATE);
        String login = shreSharedPreferences.getString("LocationProfile", "");
        return login;
    }

    public void setGPSDate(Context context, String gpsTime) {
        SharedPreferences shreSharedPreferences = context.getSharedPreferences("GPSDate", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shreSharedPreferences.edit();
        editor.putString("GPSDate", gpsTime);
        editor.commit();

    }

    public String getGPSDate(Context context) {
        SharedPreferences shreSharedPreferences = context.getSharedPreferences("GPSDate", Context.MODE_PRIVATE);
        String GPSDate = shreSharedPreferences.getString("GPSDate", "");
        return GPSDate;
    }
    // Save latitude and longitude to SharedPreferences
    public void saveLocation(String latitudeValue, String longitudeValue, Context context) {
        SharedPreferences sharedLocation = context.getSharedPreferences("AppLocation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedLocation.edit();
        editor.putString("latitude", latitudeValue);
        editor.putString("longitude", longitudeValue);
        editor.apply();  // Save changes asynchronously
    }

    // Retrieve latitude and longitude from SharedPreferences
    public void getLocation(Context context) {
        SharedPreferences sharedLocation = context.getSharedPreferences("AppLocation", Context.MODE_PRIVATE);
        String latitude = sharedLocation.getString("latitude", "0.0");
        String longitude = sharedLocation.getString("longitude", "0.0");
    }

}
