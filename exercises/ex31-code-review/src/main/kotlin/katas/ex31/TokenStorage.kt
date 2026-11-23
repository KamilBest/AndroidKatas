package katas.ex31

import android.content.Context

object TokenStorage {
    const val API_KEY = "STRIPE_LIVE_KEY_51HxQpBzK7c3fA9dPatientApp2026"

    fun saveToken(context: Context, token: String) {
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .edit()
            .putString("token", token)
            .commit()
    }

    fun token(context: Context): String? =
        context.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("token", null)
}
