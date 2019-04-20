package com.vvsemir.kindawk.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.vvsemir.kindawk.db.DbOpenHelper.Companion.DB_TABLE_FRIENDS
import com.vvsemir.kindawk.db.DbOpenHelper.Companion.DB_TABLE_NEWSFEED
import com.vvsemir.kindawk.db.DbOpenHelper.Companion.DB_TABLE_PROFILE
import com.vvsemir.kindawk.provider.*
import java.util.*

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

    fun insertNewsWall(posts: List<NewsPost>) {
        posts.forEach(){
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

            val response = insert( DbOpenHelper.DB_TABLE_NEWSFEED, values )

            if(response == null || response < 0){
                removeAllNews();

                return;
            }
        }
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

    fun getNewsWallRange(startId : Int, endId : Int) : List<NewsPost> {
        val resCurosor : Cursor? = getNewsWall(startId, endId)
        try {
            val posts = ArrayList<NewsPost>()
            val cursor = resCurosor;

            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                        var post = NewsPost()
                        post.type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
                        post.sourceId = cursor.getInt(cursor.getColumnIndexOrThrow("source_id"))
                        post.dateUnixTime = Date(cursor.getLong(cursor.getColumnIndexOrThrow("date")))
                        post.postId = cursor.getInt(cursor.getColumnIndexOrThrow("post_id"))
                        post.postText = cursor.getString(cursor.getColumnIndexOrThrow("post_text"))
                        post.sourceName = cursor.getString(cursor.getColumnIndexOrThrow("source_name"))
                        post.sourcePhotoUrl = cursor.getString(cursor.getColumnIndexOrThrow("source_photo_url"))
                        post.postPhotoUrl = cursor.getString(cursor.getColumnIndexOrThrow("post_photo_url"))

                        val sourcePhotoBytes = ContentValues()
                        sourcePhotoBytes.put(NewsPost.PHOTO_BYTES, cursor.getBlob(cursor.getColumnIndexOrThrow("source_photo_bytes")))
                        post.sourcePhoto = sourcePhotoBytes

                        val postPhotoBytes = ContentValues()
                        postPhotoBytes.put(NewsPost.PHOTO_BYTES, cursor.getBlob(cursor.getColumnIndexOrThrow("post_photo_bytes")))
                        post.postPhoto = postPhotoBytes

                        posts.add(post)

                    } while ((cursor.moveToNext()))
                }
            }

            return posts
        } finally {
            resCurosor?.close();
        }
    }

    companion object {
        private var instance: DbManager? = null

        fun getInstance(context: Context): DbManager =
                instance ?: synchronized(this) {
                    instance ?: DbManager(context)
                }

    }

    enum class DbResponse {
        DB_RESPONSE_STATUS_SUCCESS,
        DB_RESPONSE_STATUS_ERROR,
        DB_RESPONSE_STATUS_EMPTY_TABLE,
        DB_RESPONSE_STATUS_EMPTY_CURSOR
    }
}
