package com.vvsemir.kindawk.provider.gson

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.vvsemir.kindawk.provider.gson.entities.GsonNewsWall
import java.util.*

class GsonHelper {
    val gson = GsonBuilder().registerTypeAdapter(Date::class.java, DateGsonAdapter()).create()

    fun getNewsWallCount(httpResponse: String): Int? {
        val responseJson = getResponseJson(httpResponse)
        val gsonNewsWall = gson.fromJson(responseJson, GsonNewsWall::class.java)

        return gsonNewsWall.items?.size
    }

    private fun getResponseJson(httpResponse: String): JsonObject {
        val jsonHttpObject = gson.fromJson(httpResponse, JsonObject::class.java)
        return jsonHttpObject.getAsJsonObject(HTTP_RESPONSE);
    }

    companion object {
        private const val HTTP_RESPONSE = "response"
    }
}