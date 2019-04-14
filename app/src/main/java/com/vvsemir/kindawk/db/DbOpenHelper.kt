package com.vvsemir.kindawk.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbOpenHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DB_TABLE_PROFILE_CREATE)
        db?.execSQL(DB_TABLE_FRIENDS_CREATE)
        db?.execSQL(DB_TABLE_NEWSFEED_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS profile")
        onCreate(db)
    }

    companion object {
        const val DB_NAME = "Kindawk.db"
        const val DB_VERSION = 1
        const val DB_TABLE_PROFILE = "profile"
        const val DB_TABLE_FRIENDS = "friends"
        const val DB_TABLE_NEWSFEED = "newsfeed"
        const val DB_TABLE_PROFILE_CREATE = "CREATE TABLE IF NOT EXISTS $DB_TABLE_PROFILE (userId INTEGER NOT NULL PRIMARY KEY, " +
                "first_name TEXT," +
                "last_name TEXT," +
                "bdate TEXT," +
                "home_town TEXT," +
                "city TEXT," +
                "country TEXT," +
                "status TEXT," +
                "phone TEXT, " +
                "profilePhoto TEXT, " +
                "profilePhotoBytes BLOB" +
                ")"
        const val DB_TABLE_FRIENDS_CREATE = "CREATE TABLE IF NOT EXISTS $DB_TABLE_FRIENDS (id INTEGER NOT NULL PRIMARY KEY, " +
                "first_name TEXT," +
                "last_name TEXT," +
                "bdate TEXT," +
                "city TEXT," +
                "country TEXT," +
                "status TEXT," +
                "photo_100 TEXT, " +
                "photobytes BLOB" +
                ")"
        const val DB_TABLE_NEWSFEED_CREATE = "CREATE TABLE IF NOT EXISTS $DB_TABLE_NEWSFEED (post_id INTEGER NOT NULL PRIMARY KEY, " +
                "type TEXT," +
                "source_id INTEGER," +
                "last_name TEXT," +
                "date INTEGER," +
                "post_text TEXT," +
                "post_photo_url TEXT, " +
                "post_photo_Bytes BLOB," +
                "source_photo_100url TEXT, " +
                "source_photo_100Bytes BLOB" +
                ")"
    }
}