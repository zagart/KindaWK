package com.vvsemir.kindawk.auth

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper (context : Context, name: String){
    val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = sharedPreferences.edit()
        operation(editor)
        editor.apply()
    }

    fun set(key: String, value: Any?) {
        when (value) {
            is String? -> edit{ it.putString(key, value) }
            is Int -> edit{ it.putInt(key, value) }
            is Boolean -> edit{ it.putBoolean(key, value) }
            is Float -> edit{ it.putFloat(key, value) }
            is Long -> edit{ it.putLong(key, value) }
            else -> throw UnsupportedOperationException("")
        }
    }

    /*inline fun <reified T: Any> get(key: String, defaultValue: T? = null): T? {
        return when (T::class) {
            String::class -> sharedPreferences.getString(key, defaultValue as? String) as T?
            Int::class -> sharedPreferences.getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> sharedPreferences.getBoolean(key, defaultValue as? Boolean ?: false) as T?
            Float::class -> sharedPreferences.getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> sharedPreferences.getLong(key, defaultValue as? Long ?: -1) as T?
            else -> throw UnsupportedOperationException("")
        }
    }*/
    fun <T: Any> get(key: String, defaultValue: T): T? {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue ) as T?
            is Int -> sharedPreferences.getInt(key, defaultValue ) as T?
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue ) as T?
            is Float -> sharedPreferences.getFloat(key, defaultValue ) as T?
            is Long -> sharedPreferences.getLong(key, defaultValue ) as T?
            else -> throw UnsupportedOperationException("")
        }
    }


    fun contains(key: String) = sharedPreferences.contains(key)

    fun clear() = edit{ it.clear() }
}
