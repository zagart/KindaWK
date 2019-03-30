package com.vvsemir.kindawk.auth

class AccessToken (var userId: Int? = 0, var accessToken: String? = null){

    fun isValid() : Boolean {
        if(userId == 0 || accessToken == null || accessToken == EMPTY_TOKEN) {
            return false
        }

        return true
    }

    fun updateToken(id: Int? = 0, token: String? = null){
        userId = id
        accessToken = token
    }

    fun accessTokenToString() = ACCESS_TOKEN + "=" + accessToken;

    companion object {
        private const val USER_ID = "user_id"
        private const val ACCESS_TOKEN = "access_token"
        private const val EMPTY_TOKEN = ""
    }

}