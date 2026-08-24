package com.iftikar.outlier.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.iftikar.outlier.core.data.di.IoDispatcher
import com.iftikar.outlier.core.models.Session
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @param:IoDispatcher private val io: CoroutineDispatcher
) {
    private val ACCESS_TOKEN = stringPreferencesKey("accessToken")
    private val REFRESH_TOKEN = stringPreferencesKey("refreshToken")

    /**
     * This will be used to save access and refresh session on first login
     */
    suspend fun saveTokensOnFirstLogin(session: Session) = withContext(io) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = session.accessToken
            prefs[REFRESH_TOKEN] = session.refreshToken
        }
    }

    /**
     * This will be called to get a new access token using the refresh token
     */
    suspend fun saveAccessTokenWhenExpired(accessToken: String) = withContext(io) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun getAccessToken(): String? = withContext(io) {
        val prefs = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .first()
        prefs[ACCESS_TOKEN]
    }


    suspend fun getRefreshToken(): String? = withContext(io) {
        val prefs = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .first()
        prefs[REFRESH_TOKEN]
    }

    /**
     * Call on logout
     */
    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

}
