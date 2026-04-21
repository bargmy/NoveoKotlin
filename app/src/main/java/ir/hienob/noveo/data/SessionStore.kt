package ir.hienob.noveo.data

import android.content.Context

class SessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("noveo_session", Context.MODE_PRIVATE)

    fun read(): Session? {
        val userId = prefs.getString("user_id", null)
        val token = prefs.getString("token", null)
        if (userId.isNullOrBlank() || token.isNullOrBlank()) return null
        return Session(
            userId = userId,
            token = token,
            sessionId = prefs.getString("session_id", "") ?: "",
            expiresAt = prefs.getLong("expires_at", 0L)
        )
    }

    fun write(session: Session) {
        prefs.edit()
            .putString("user_id", session.userId)
            .putString("token", session.token)
            .putString("session_id", session.sessionId)
            .putLong("expires_at", session.expiresAt)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
