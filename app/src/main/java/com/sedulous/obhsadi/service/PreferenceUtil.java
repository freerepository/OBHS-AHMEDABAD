package com.sedulous.obhsadi.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by algosofttechnologies on 11/24/17.
 */

public class PreferenceUtil {

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getUserIdSelected(Context context) {
        return getSharedPreferences(context).getString("userId", "");
    }

    public static void setUserIdSelected(Context context, String userId) {
        getSharedPreferences(context).edit().putString("userId", userId).apply();
    }

    public static String getUserMobile(Context context) {
        return getSharedPreferences(context).getString("mobileNumber", "");
    }

    public static void setUserMobile(Context context, String mobileNumber) {
        getSharedPreferences(context).edit().putString("mobileNumber", mobileNumber).apply();
    }

    public static String getUserPassword(Context context) {
        return getSharedPreferences(context).getString("password", "");
    }

    public static void setUserPassword(Context context, String password) {
        getSharedPreferences(context).edit().putString("password", password).apply();
    }

    public static String getUserEmail(Context context) {
        return getSharedPreferences(context).getString("email", "");
    }

    public static void setUserEmail(Context context, String email) {
        getSharedPreferences(context).edit().putString("email", email).apply();
    }

    public static String getUserType(Context context) {
        return getSharedPreferences(context).getString("userType", "");
    }

    public static void setUserType(Context context, String userType) {
        getSharedPreferences(context).edit().putString("userType", userType).apply();
    }
    public static String getDepot(Context context) {
        return getSharedPreferences(context).getString("depot_code", "");
    }
    public static void setDepot(Context context, String userType) {
        getSharedPreferences(context).edit().putString("depot_code", userType).apply();
    }
    public static String getDesignation(Context context) {
        return getSharedPreferences(context).getString("designation", "");
    }
    public static void setDesignation(Context context, String userType) {
        getSharedPreferences(context).edit().putString("designation", userType).apply();
    }
    public static void clearPref(Context context){
          getSharedPreferences(context).edit().clear().apply();
    }
}
