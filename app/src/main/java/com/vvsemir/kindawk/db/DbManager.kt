package com.vvsemir.kindawk.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.vvsemir.kindawk.db.DbOpenHelper.Companion.DB_TABLE_FRIENDS
import com.vvsemir.kindawk.db.DbOpenHelper.Companion.DB_TABLE_NEWSFEED
import com.vvsemir.kindawk.db.DbOpenHelper.Companion.DB_TABLE_PROFILE
import com.vvsemir.kindawk.provider.*

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

    fun getNewsWall(startId : Int, endId : Int): Cursor? {
        return query(prepareSqlGetNewsWall(startId, endId))
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

    fun insertNewsWall(newsWall : NewsWall) : Long? {
        var resultIds : Long = 1
        newsWall.news.forEach(){
            val values = ContentValues()

            values.put("type" , it.type)
            values.put("source_id" , it.sourceId)
            values.put("date" , it.dateUnixTime.time)
            values.put("post_id" , it.postId)
            values.put("post_text" , it.postText)
            values.put("source_name" , it.sourceName)
            values.put("source_photo_url" , it.sourcePhotoUrl)
            values.put("post_photo_url" , it.postPhotoUrl)

            if(it.sourcePhoto != null) {
                values.put("source_photo_bytes", it.sourcePhoto.getAsByteArray(NewsPost.PHOTO_BYTES))
            }

            if(it.postPhoto != null) {
                values.put("post_photo_bytes", it.postPhoto.getAsByteArray(NewsPost.PHOTO_BYTES))
            }

            resultIds *= insert( DbOpenHelper.DB_TABLE_NEWSFEED, values ) ?: -1
        }

        return resultIds
    }


    private  fun prepareSqlGetUserProfile(userId : Int): String {
        return "SELECT * FROM $DB_TABLE_PROFILE WHERE userId = $userId"
    }

    private  fun prepareSqlGetFriends(): String {
        return "SELECT * FROM $DB_TABLE_FRIENDS"
    }

    private  fun prepareSqlGetNewsWall(startId : Int, endId : Int): String {
        return "SELECT * FROM $DB_TABLE_NEWSFEED WHERE _id BETWEEN $startId AND $endId"
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
