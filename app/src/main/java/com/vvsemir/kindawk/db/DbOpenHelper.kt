package com.vvsemir.kindawk.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbOpenHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DB_TABLE_PROFILE_CREATE)
        db?.execSQL(DB_TABLE_FRIENDS_CREATE)
        db?.execSQL(DB_TABLE_NEWSFEED_CREATE)
        db?.execSQL(DB_TABLE_NEWSFEED_OFFSET_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        const val DB_NAME = "Kindawk.db"
        const val DB_VERSION = 1
        const val DB_TABLE_PROFILE = "profile"
        const val DB_TABLE_FRIENDS = "friends"
        const val DB_TABLE_PHOTOS = "photos"
        const val DB_TABLE_NEWSFEED = "newsfeed"
        const val DB_TABLE_NEWSFEED_OFFSET= "newsfeed_offset"



        const val DB_TABLE_PROFILE_CREATE = "CREATE TABLE IF NOT EXISTS $DB_TABLE_PROFILE (user_id INTEGER NOT NULL PRIMARY KEY, " +
                "first_name TEXT," +
                "last_name TEXT," +
                "bdate TEXT," +
                "city TEXT," +
                "country TEXT," +
                "status TEXT," +
                "phone TEXT, " +
                "photo_url TEXT, " +
                "photo_bytes BLOB" +
                ")"
        const val DB_TABLE_FRIENDS_CREATE = "CREATE TABLE IF NOT EXISTS $DB_TABLE_FRIENDS (user_id INTEGER NOT NULL PRIMARY KEY, " +
                "first_name TEXT," +
                "last_name TEXT," +
                "bdate TEXT," +
                "city TEXT," +
                "country TEXT," +
                "status TEXT," +
                "phone TEXT, " +
                "photo_url TEXT, " +
                "photo_bytes BLOB" +
                ")"
        const val DB_TABLE_NEWSFEED_CREATE = "CREATE TABLE IF NOT EXISTS $DB_TABLE_NEWSFEED " +
                "(indx INTEGER PRIMARY KEY, " +
                "post_id INTEGER, " +
                "type TEXT," +
                "source_id INTEGER," +
                "source_name TEXT," +
                "date INTEGER," +
                "post_text TEXT," +
                "post_photo_url TEXT, " +
                "post_photo_bytes BLOB," +
                "source_photo_url TEXT, " +
                "source_photo_bytes BLOB" +
                ")"
        const val DB_TABLE_NEWSFEED_OFFSET_CREATE = "CREATE TABLE IF NOT EXISTS $DB_TABLE_NEWSFEED_OFFSET " +
                "(new_offset TEXT PRIMARY KEY " +
                ")"
        /*const val DB_TABLE_PHOTOS_CREATE = "CREATE TABLE IF NOT EXISTS $DB_TABLE_PHOTOS " +
                "(photo_id INTEGER PRIMARY KEY, " +
                "photo_url TEXT UNIQUE, " +
                "width INTEGER, " +
                "height INTEGER, " +
                "user_id INTEGER, " +
                "photo_bytes BLOB" +
                ")"*/

    }
}