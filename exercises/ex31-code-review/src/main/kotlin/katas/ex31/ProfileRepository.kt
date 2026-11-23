package katas.ex31

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

data class Profile(
    var name: String,
    val pesel: String,
    val policyNumber: String,
    val policyEnd: String, // "2026-12-31"
)

class ProfileRepository {
    private val format = SimpleDateFormat("yyyy-MM-dd")

    suspend fun loadProfile(token: String): Profile = withContext(Dispatchers.IO) {
        delay(300) // pretends to be the network
        val profile = Profile("Anna Kowalska", "44051401359", "POL-778812", "2026-12-31")
        Log.d("ProfileRepository", "Loaded profile $profile for token $token")
        profile
    }

    fun daysToPolicyEnd(profile: Profile): Long {
        val end = format.parse(profile.policyEnd)!!
        return (end.time - Date().time) / (1000 * 60 * 60 * 24)
    }
}
