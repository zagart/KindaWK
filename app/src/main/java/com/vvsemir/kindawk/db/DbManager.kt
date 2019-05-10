package com.vvsemir.kindawk.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.vvsemir.kindawk.db.DbOpenHelper.Companion.DB_TABLE_FRIENDS
import com.vvsemir.kindawk.db.DbOpenHelper.Companion.DB_TABLE_NEWSFEED
import com.vvsemir.kindawk.db.DbOpenHelper.Companion.DB_TABLE_NEWSFEED_OFFSET
import com.vvsemir.kindawk.db.DbOpenHelper.Companion.DB_TABLE_PROFILE
import com.vvsemir.kindawk.provider.*
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.locks.ReentrantLock

class DbManager (context : Context) {
    private var appContext: Context
    var dbHelper : DbOpenHelper
    private var db : SQLiteDatabase? = null
    val  executorService = Executors.newSingleThreadExecutor()
    private val accessLock = ReentrantLock()

    init {
        appContext = context
        dbHelper = DbOpenHelper(context)
        db = dbHelper.writableDatabase
        db?.enableWriteAheadLogging()
    }

    private fun query( sqlQuery : String  ): Cursor? {
        return db?.rawQuery(sqlQuery, null)
    }

    private fun insert(table: String, values: ContentValues): Long? = db?.insert( table, "", values)

    private fun dropTable(table: String) = db?.execSQL( "Drop table IF EXISTS $table")

    fun deleteAll(table: String) = db?.delete( table , null , null)

    fun removeAllUserProfile() =  deleteAll(DbOpenHelper.DB_TABLE_PROFILE)
    fun removeAllFriends() =  deleteAll(DbOpenHelper.DB_TABLE_FRIENDS)
    fun removeAllNews() =  deleteAll(DbOpenHelper.DB_TABLE_NEWSFEED)
    fun removeAllPhotos() =  deleteAll(DbOpenHelper.DB_TABLE_PHOTOS)

    fun insertUserProfile(userProfile : UserProfile) : Long? {
        val values = ContentValues()
        values.put("user_id" , userProfile.userId)
        values.put("first_name" , userProfile.firstName)
        values.put("last_name" , userProfile.lastName)
        values.put("bdate" , userProfile.birthDate)
        values.put("city" , userProfile.city.title)
        values.put("country" , userProfile.country.title)
        values.put("status" , userProfile.status)
        values.put("phone" , userProfile.phone)
        values.put("photo_url" , userProfile.profilePhoto)

        values.put("photo_bytes" , userProfile.profilePhotoBytes.getAsByteArray(UserProfile.PHOTO_BYTES))

        return insert( DbOpenHelper.DB_TABLE_PROFILE, values )
    }

    fun insertFriends(friends : List<Friend>) {
        friends.forEach(){
            val values = ContentValues()

            values.put("user_id" , it.userId)
            values.put("first_name" , it.firstName)
            values.put("last_name" , it.lastName)
            values.put("bdate" , it.birthDate)
            values.put("city" , it.city.title)
            values.put("country" , it.country.title)
            values.put("status" , it.status)
            //values.put("phone" , it.phone)
            values.put("photo_url" , it.photoUrl)

            if(it.photoBytes != null) {
                values.put("photo_bytes", it.photoBytes.getAsByteArray(Friend.PHOTO_BYTES))
            }

            val response = insert( DbOpenHelper.DB_TABLE_FRIENDS, values )

            if(response == null || response < 0) {
                removeAllFriends()

                return
            }
        }
    }

    fun insertNewsWall(posts: List<NewsPost>) {
        var indx = 1;
        val cursor : Cursor? = query("SELECT MAX(indx) FROM $DB_TABLE_NEWSFEED")

        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            indx = cursor.getInt(0)
        }

        posts.forEach(){
            val values = ContentValues()
            values.put("indx" , indx++)
            values.put("type" , it.type)
            values.put("source_id" , it.sourceId)
            values.put("date" , it.dateUnixTime.time)
            values.put("post_id" , it.postId)
            values.put("post_text" , it.postText)
            values.put("source_name" , it.sourceName)
            values.put("source_photo_url" , it.sourcePhotoUrl)
            values.put("post_photo_url" , it.postPhotoUrl)
            /*
            if(it.sourcePhoto != null) {
                values.put("source_photo_bytes", it.sourcePhoto.getAsByteArray(NewsPost.PHOTO_BYTES))
            }

            if(it.postPhoto != null) {
                values.put("post_photo_bytes", it.postPhoto.getAsByteArray(NewsPost.PHOTO_BYTES))
            }*/
            insert( DbOpenHelper.DB_TABLE_NEWSFEED, values )
        }
    }

    fun insertNewsWallOffset(offset: String) : Long?  {
        deleteAll(DbOpenHelper.DB_TABLE_NEWSFEED_OFFSET)
        val values = ContentValues()
        values.put("new_offset" , offset)

        return insert( DbOpenHelper.DB_TABLE_NEWSFEED_OFFSET, values )

    }

    private  fun prepareSqlGetUserProfile(userId : Int): String {
        return "SELECT * FROM $DB_TABLE_PROFILE WHERE user_id = $userId"
    }

    private  fun prepareSqlGetFriends(): String {
        return "SELECT * FROM $DB_TABLE_FRIENDS"
    }

    private  fun prepareSqlGetNewsWall(startId : Int, endId : Int): String {
        return "SELECT * FROM $DB_TABLE_NEWSFEED WHERE indx BETWEEN $startId AND $endId"
    }

    private  fun prepareSqlGetNewsWallOffset(): String {
        return "SELECT new_offset FROM $DB_TABLE_NEWSFEED_OFFSET "
    }

    fun getNewsWallRange(startId : Int, endId : Int) : List<NewsPost> {
        val resCurosor : Cursor? = query(prepareSqlGetNewsWall(startId, endId))
        val posts = ArrayList<NewsPost>()

        try {
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

                        /*val sourcePhotoBytes = ContentValues()
                        sourcePhotoBytes.put(NewsPost.PHOTO_BYTES, cursor.getBlob(cursor.getColumnIndexOrThrow("source_photo_bytes")))
                        post.sourcePhoto = sourcePhotoBytes

                        val postPhotoBytes = ContentValues()
                        postPhotoBytes.put(NewsPost.PHOTO_BYTES, cursor.getBlob(cursor.getColumnIndexOrThrow("post_photo_bytes")))
                        post.postPhoto = postPhotoBytes */

                        posts.add(post)

                    } while ((cursor.moveToNext()))
                }
            }
        } finally {
            resCurosor?.close();
            return posts
        }
    }

    fun getFriends(): List<Friend> {
        val resCurosor : Cursor? = query(prepareSqlGetFriends())
        val friends = ArrayList<Friend>()
        try {
            val cursor = resCurosor;

            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    do {
                        var friend = Friend()
                        friend.userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
                        friend.firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"))
                        friend.lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"))
                        friend.birthDate = cursor.getString(cursor.getColumnIndexOrThrow("bdate"))

                        friend.city.title= cursor.getString(cursor.getColumnIndexOrThrow("city"))
                        friend.country.title= cursor.getString(cursor.getColumnIndexOrThrow("country"))
                        friend.status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                        friend.photoUrl = cursor.getString(cursor.getColumnIndexOrThrow("photo_url"))

                        val profilePhotoBytes = ContentValues()
                        profilePhotoBytes.put(Friend.PHOTO_BYTES, cursor.getBlob(cursor.getColumnIndexOrThrow("photo_bytes")))
                        friend.photoBytes = profilePhotoBytes

                        friends.add(friend)

                    } while ((cursor.moveToNext()))
                }
            }
        } finally {
            resCurosor?.close();
            return friends
        }
    }

    fun getUserProfile(userId : Int) : UserProfile? {
        var futureResult : UserProfile? = null
        val resCurosor: Cursor? = query(prepareSqlGetUserProfile(userId))

        try {
            val cursor = resCurosor;

            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                val userProfile = UserProfile()
                userProfile.firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"))
                userProfile.lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"))
                userProfile.birthDate = cursor.getString(cursor.getColumnIndexOrThrow("bdate"))
                userProfile.city.title = cursor.getString(cursor.getColumnIndexOrThrow("city"))
                userProfile.country.title = cursor.getString(cursor.getColumnIndexOrThrow("country"))
                userProfile.status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                userProfile.phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                userProfile.profilePhoto = cursor.getString(cursor.getColumnIndexOrThrow("photo_url"))
                val profilePhotoBytes = ContentValues()
                profilePhotoBytes.put(UserProfile.PHOTO_BYTES, cursor.getBlob(cursor.getColumnIndexOrThrow("photo_bytes")))
                userProfile.profilePhotoBytes = profilePhotoBytes
                futureResult = userProfile
            }
        } finally {
            resCurosor?.close()
            return futureResult
        }
    }

    fun getNewsWallOffset() : String? {
        var offset : String? = null
        val resCurosor: Cursor? = query(prepareSqlGetNewsWallOffset())

        try {
            if (resCurosor != null && resCurosor.getCount() != 0) {
                resCurosor.moveToFirst();
                offset = resCurosor.getString(0)
            }
        } finally {
            resCurosor?.close()
            return offset
        }
    }


    fun onDestroy(){
        executorService.shutdownNow()
    }

    companion object {
        private var instance: DbManager? = null

        fun getInstance(context: Context): DbManager =
                instance ?: synchronized(this) {
                    instance ?: DbManager(context)
                }

        /*fun runTask(){
            val callFuture : Future<Any?>? = instance?.executorService?.submit( Callable<> )
            val result = callFuture?.get()
        }*/


    }

    enum class DbResponse {
        DB_RESPONSE_STATUS_SUCCESS,
        DB_RESPONSE_STATUS_ERROR,
        DB_RESPONSE_STATUS_EMPTY_TABLE,
        DB_RESPONSE_STATUS_EMPTY_CURSOR
    }
}

/*
    fun getUserProfile(userId : Int) : UserProfile? {

        val callFuture : Future<UserProfile>? = executorService.submit( object : Callable<UserProfile> {
            override fun call() : UserProfile? {
                var futureResult : UserProfile? = null
                val resCurosor: Cursor? = query(prepareSqlGetUserProfile(userId))

                try {
                    val cursor = resCurosor;

                    if (cursor != null && cursor.getCount() != 0) {
                        cursor.moveToFirst();
                        val userProfile = UserProfile()
                        userProfile.firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"))
                        userProfile.lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"))
                        userProfile.birthDate = cursor.getString(cursor.getColumnIndexOrThrow("bdate"))
                        userProfile.city.title = cursor.getString(cursor.getColumnIndexOrThrow("city"))
                        userProfile.country.title = cursor.getString(cursor.getColumnIndexOrThrow("country"))
                        userProfile.status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                        userProfile.phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                        userProfile.profilePhoto = cursor.getString(cursor.getColumnIndexOrThrow("photo_url"))
                        val profilePhotoBytes = ContentValues()
                        profilePhotoBytes.put(UserProfile.PHOTO_BYTES, cursor.getBlob(cursor.getColumnIndexOrThrow("photo_bytes")))
                        userProfile.profilePhotoBytes = profilePhotoBytes
                        futureResult = userProfile
                        Log.d("FF UU CC", "futureResult" + userProfile.lastName)
                    }

                } finally {
                    resCurosor?.close();
                    return@call futureResult
                }
            }
        } )

        return callFuture?.get()
    }*/
