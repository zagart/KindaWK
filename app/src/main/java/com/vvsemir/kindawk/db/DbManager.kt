package com.vvsemir.kindawk.db

import android.content.Context

class DbManager (context : Context) {
    private var appContext: Context
    var dbHelper : DbOpenHelper

    init {
        appContext = context
        dbHelper = DbOpenHelper(context)
    }

    private fun isLoggedIn(): Boolean = true


    private fun saveTokenInPreferences(url : String) {

    }

    companion object {
        private var instance: DbManager? = null

        fun getInstance(context: Context): DbManager =
                instance ?: synchronized(this) {
                    instance ?: DbManager(context)
                }


        //@JvmStatic
       // fun getCurrentToken()  = getInstance().appContext
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
