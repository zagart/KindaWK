package com.vvsemir.kindawk.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.vvsemir.kindawk.provider.Friend
import com.vvsemir.kindawk.provider.FriendsList
import com.vvsemir.kindawk.provider.UserProfile

class DbManager (context : Context) {
    private var appContext: Context
    var dbHelper : DbOpenHelper
    private var db : SQLiteDatabase? = null

    init {
        appContext = context
        dbHelper = DbOpenHelper(context)
        db = dbHelper.writableDatabase
    }

    private fun query( sqlQuery : String  ): Cursor? {
        return db?.rawQuery(sqlQuery, null)
    }

    private fun insert(table: String, values: ContentValues): Long? = db?.insert( table, "", values)

    private fun deleteAll(table: String) = db?.delete( table , null , null)

    private fun dropTable(table: String) = db?.execSQL( "Drop table IF EXISTS $table")

    fun getUserProfile(userId : Int): Cursor? {
        return query(prepareSqlGetUserProfile(userId))
    }
    fun getFriends(): Cursor? {
        return query(prepareSqlGetFriends())
    }

    fun removeAllUserProfile() =  deleteAll(DbOpenHelper.DB_TABLE_PROFILE)
    fun removeAllFriends() =  deleteAll(DbOpenHelper.DB_TABLE_FRIENDS)
    fun removeAllNews() =  deleteAll(DbOpenHelper.DB_TABLE_NEWSFEED)

    fun insertUserProfile(userProfile : UserProfile) : Long? {
        val values = ContentValues()
        values.put("profilePhotoBytes" , userProfile.profilePhotoBytes.getAsByteArray(UserProfile.PHOTO_BYTES))
        values.put("first_name" , userProfile.firstName)
        values.put("userId" , userProfile.userId)

        return insert( DbOpenHelper.DB_TABLE_PROFILE, values )
    }

    fun insertFriends(friendsList : FriendsList) : Long? {
        var resultIds : Long = 1
        friendsList.list.forEach(){
            val values = ContentValues()
            values.put("photobytes" , it.photo100Bytes.getAsByteArray(Friend.PHOTO_BYTES))
            values.put("first_name" , it.firstName)
            values.put("last_name" , it.lastName)
            values.put("id" , it.uid)
            values.put("country" , it.country.title)

            resultIds *= insert( DbOpenHelper.DB_TABLE_FRIENDS, values ) ?: -1
        }

        return resultIds
    }


    private  fun prepareSqlGetUserProfile(userId : Int): String {
        return "SELECT * FROM profile WHERE userId = $userId"
    }

    private  fun prepareSqlGetFriends(): String {
        return "SELECT * FROM friends"
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
