package dev.esandamzapp.slatrackerapp.data.local

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "sla_tracker_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val TOKEN_KEY = "auth_token"
        private const val USER_ID_KEY = "user_id"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun saveUserId(userId: Int) {
        prefs.edit().putInt(USER_ID_KEY, userId).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(USER_ID_KEY, -1)
    }

    fun clearToken() {
        prefs.edit().remove(TOKEN_KEY).remove(USER_ID_KEY).apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
