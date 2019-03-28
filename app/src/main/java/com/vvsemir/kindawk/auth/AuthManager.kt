package com.vvsemir.kindawk.auth

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.content.LocalBroadcastManager
import android.webkit.CookieManager
import com.vvsemir.kindawk.Models.Constants.*
import com.vvsemir.kindawk.utils.Utilits

class AuthManager private constructor(context : Context) {
    private var appContext: Context
    var accessToken: AccessToken

    init {
        appContext = context
        accessToken = getTokenFromPreferences()
    }

    private fun isLoggedIn(): Boolean = accessToken.isValid()

    private fun logout() {
        getPreferences().edit().clear().apply();
        CookieManager.getInstance().removeAllCookie();
        accessToken.updateToken()
    }

    private fun saveTokenInPreferences(url : String) {
        val params = parseUrlForToken(url)

        val editor : SharedPreferences.Editor = getPreferences().edit()
        val token = params[PREFERENCE_ACCESS_TOKEN]
        val userId = params[PREFERENCE_USER_ID]

        if (token != null && userId != null) {
            editor.putString(PREFERENCE_ACCESS_TOKEN, token);
            editor.putString(PREFERENCE_USER_ID, userId);
            editor.apply();
            accessToken.updateToken(Integer.parseInt(userId), token)
        }
    }

    private fun authUrl() : String = "https://oauth.vk.com/authorize?client_id=" + APP_VKCLIENT_ID +
        "&scope=" +  APP_VKCLIENT_SCOPE +
        "&redirect_uri=https://oauth.vk.com/blank.html" +
        "&display=mobile" +
        "&v=" + APP_VKCLIENT_API_V +
        "&response_type=token";

    private fun parseUrlForToken(url : String ) : HashMap<String, String> {
        val result = HashMap<String, String>()
        result[PREFERENCE_ACCESS_TOKEN] = Utilits.extractPattern(url, URL_TOKEN_PATTERN)
        result[PREFERENCE_USER_ID] = Utilits.extractPattern(url, URL_USERID_PATTERN)

        return result
    }

    private fun getTokenFromPreferences(): AccessToken {
        if(!getPreferences().contains(PREFERENCE_USER_ID) || !getPreferences().contains(PREFERENCE_ACCESS_TOKEN) ) {
            return AccessToken()
        }

        return AccessToken(Integer.parseInt(getPreferences().getString(PREFERENCE_USER_ID, "") ?: "0"),
                getPreferences().getString(PREFERENCE_ACCESS_TOKEN, ""));
    }

    private fun getPreferences() = appContext.getSharedPreferences(PREFERENCE_APP_NAME, Context.MODE_PRIVATE)


    companion object : SingletonHolder<AuthManager, Context>(::AuthManager){
        const val PREFERENCE_APP_NAME = "com.vvsemir.kindawk.app_prefs"
        const val PREFERENCE_USER_ID = "user_id"
        const val PREFERENCE_ACCESS_TOKEN = "token"
        const val URL_TOKEN_PATTERN = "_token=(.*?)&"
        const val URL_USERID_PATTERN = "user_id=(\\d*)"
        const val APP_VKCLIENT_ID = "6870401"
        const val APP_VKCLIENT_SCOPE = "wall,offline,friends,photos"
        const val APP_VKCLIENT_AUTH_REDIRECT = "https://oauth.vk.com/blank.html"

        @JvmStatic
        fun getCurrentToken()  = getInstance()?.accessToken

        @JvmStatic
        fun isUserLoggedIn()  = getInstance()?.isLoggedIn()

        @JvmStatic
        fun getAuthUrl()  = getInstance()?.authUrl()

        @JvmStatic
        fun saveTokenAfterLogin(url : String) = getInstance()?.saveTokenInPreferences( url )

        @JvmStatic
        fun userLogout()  = getInstance()?.logout()

    }
}

/*
companion object {
    private var instance: AuthManager? = null

    fun getInstance(context: Context): AuthManager =
            instance ?: synchronized(this) {
                instance ?: AuthManager(context)
            }
*/
