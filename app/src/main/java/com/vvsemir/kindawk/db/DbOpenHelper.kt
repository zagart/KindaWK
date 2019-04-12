package com.vvsemir.kindawk.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbOpenHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DB_TABLE_PROFILE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS profile")
        onCreate(db)
    }

    companion object {
        const val DB_NAME = "Kindawk.db"
        const val DB_VERSION = 1
        const val DB_TABLE_PROFILE = "profile"
        const val DB_TABLE_PROFILE_CREATE = "CREATE TABLE IF NOT EXISTS profile (userId INTEGER NOT NULL PRIMARY KEY, " +
                "first_name TEXT," +
                "last_name TEXT," +
                "bdate TEXT," +
                "home_town TEXT," +
                "country TEXT," +
                "status TEXT," +
                "phone TEXT, " +
                "profilePhoto TEXT, " +
                "profilePhotoBytes BLOB" +
                ")"
    }
}