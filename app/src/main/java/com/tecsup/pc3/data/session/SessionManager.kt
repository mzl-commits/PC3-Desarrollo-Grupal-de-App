package com.tecsup.pc3.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_session",
)

data class UserSession(
    val isLoggedIn: Boolean,
    val username: String,
)

class SessionManager(context: Context) {
    private val dataStore = context.applicationContext.sessionDataStore

    val session: Flow<UserSession> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val username = preferences[Keys.USERNAME].orEmpty()
            UserSession(
                // Una bandera huérfana nunca debe permitir acceso a rutas protegidas.
                isLoggedIn = preferences[Keys.IS_LOGGED_IN] == true && username.isNotBlank(),
                username = username,
            )
        }

    suspend fun saveSession(username: String) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_LOGGED_IN] = true
            preferences[Keys.USERNAME] = username
        }
    }

    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }

    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USERNAME = stringPreferencesKey("username")
    }
}
