package com.vvsemir.kindawk.auth

import android.content.Context
import android.webkit.CookieManager
import com.vvsemir.kindawk.utils.Utilits

class AuthManager private constructor(context : Context) {
    val appContext: Context
    val accessToken: AccessToken
    val preferenceHelper : PreferenceHelper

    init {
        appContext = context
        preferenceHelper = PreferenceHelper(context, PREFERENCE_APP_NAME)
        accessToken = getTokenFromPreferences()
    }

    private fun isLoggedIn(): Boolean = accessToken.isValid()

    private fun logout() {
        preferenceHelper.clear()
        CookieManager.getInstance().removeAllCookie();
        accessToken.updateToken()
    }

    private fun saveTokenInPreferences(url : String) {
        val params = parseUrlForToken(url)
        val token = params[PREFERENCE_ACCESS_TOKEN]
        val userId = params[PREFERENCE_USER_ID]

        if (token != null && userId != null) {
            preferenceHelper.set(PREFERENCE_ACCESS_TOKEN, token)
            preferenceHelper.set(PREFERENCE_USER_ID, userId)
            accessToken.updateToken(Integer.parseInt(userId), token)
        }
    }

    private fun authUrl() : String = "https://oauth.vk.com/authorize?client_id=" + APP_VKCLIENT_ID +
        "&scope=" +  APP_VKCLIENT_SCOPE +
        "&redirect_uri=https://oauth.vk.com/blank.html" +
        "&display=mobile" +
        "&" + APP_VKCLIENT_API_V +
        "&response_type=token";

    private fun parseUrlForToken(url : String ) : HashMap<String, String> {
        val result = HashMap<String, String>()
        result[PREFERENCE_ACCESS_TOKEN] = Utilits.extractPattern(url, URL_TOKEN_PATTERN)
        result[PREFERENCE_USER_ID] = Utilits.extractPattern(url, URL_USERID_PATTERN)

        return result
    }

    private fun getTokenFromPreferences(): AccessToken {
        if(!preferenceHelper.contains(PREFERENCE_USER_ID) || !preferenceHelper.contains(PREFERENCE_ACCESS_TOKEN) ) {
            return AccessToken()
        }

        return AccessToken(Integer.parseInt(preferenceHelper.get(PREFERENCE_USER_ID, "") ?: "0"),
                preferenceHelper.get(PREFERENCE_ACCESS_TOKEN, ""));
    }

    //private fun getPreferences() = appContext.getSharedPreferences(PREFERENCE_APP_NAME, Context.MODE_PRIVATE)
    //private fun getPreferences() = preferenceHelper.sharedPreferences


    companion object : SingletonHolder<AuthManager, Context>(::AuthManager){
        const val PREFERENCE_APP_NAME = "com.vvsemir.kindawk.app_prefs"
        const val PREFERENCE_USER_ID = "user_id"
        const val PREFERENCE_ACCESS_TOKEN = "token"
        const val PREFERENCE_NEWS_DATE_POSTED = "news_posted_range"
        const val PREFERENCE_NEWS_CHECK_DELAY = "news_posted_check_delay"
        const val URL_TOKEN_PATTERN = "_token=(.*?)&"
        const val URL_USERID_PATTERN = "user_id=(\\d*)"
        const val APP_VKCLIENT_ID = "6870401"
        const val APP_VKCLIENT_SCOPE = "wall,offline,friends,photos,status,notify,groups,notifications"
        const val APP_VKCLIENT_AUTH_REDIRECT = "https://oauth.vk.com/blank.html"
        const val APP_VKCLIENT_API_V = "v=5.92";

        @JvmStatic
        fun getCurrentToken()  = getInstance()?.accessToken

        @JvmStatic
        fun getCurrentContext()  = getInstance()?.appContext

        @JvmStatic
        fun getAppPreferences()  = getInstance()?.preferenceHelper

        @JvmStatic
        fun getTokenVersionString()  = getInstance()?.accessToken?.accessTokenToString() + "&" + APP_VKCLIENT_API_V


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
