package com.uitcontest.studymanagement;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "smarthoctapPrefs";
    private static final String AUTH_TOKEN = "auth_token";
    private static SharedPrefManager instance;
    private final Context context;
    private final SharedPreferences sharedPreferences;


    public SharedPrefManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    // Store the token
    public void saveAuthToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN, token);
        editor.apply();
    }

    // Retrieve the token
    public String getAuthToken() {
        return sharedPreferences.getString(AUTH_TOKEN, null);
    }

    // Clear the token
    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(AUTH_TOKEN);
        editor.apply();
    }
}
