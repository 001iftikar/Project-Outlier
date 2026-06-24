package com.iftikar.outlier.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val EXPIRE = stringPreferencesKey("expire")
    private val USER_ID = stringPreferencesKey("userId")

    /**
     * Save userId and expire, expire to check session expiration and userId to avoid making network call everytime to get the userId
     */
    suspend fun saveSession(expiry: String, userId: String) {
        dataStore.edit { prefs ->
            prefs[EXPIRE] = expiry
            prefs[USER_ID] = userId
        }
    }

    /**
     * Call on logout
     */
    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    suspend fun getSessionExpiry(): String? {
        val prefs = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .first()
        return prefs[EXPIRE]
    }

    suspend fun getUserId(): String? {
        val prefs = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .first()
        return prefs[USER_ID]
    }

}