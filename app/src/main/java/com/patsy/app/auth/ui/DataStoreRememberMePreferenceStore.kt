package com.patsy.app.auth.ui

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException

private val Context.finalAuthPolicyDataStore by preferencesDataStore(name = "final_auth_policy")

class DataStoreRememberMePreferenceStore(context: Context) : RememberMePreferenceStore {
    private val dataStore = context.applicationContext.finalAuthPolicyDataStore

    override suspend fun isSessionRestoreEnabled(): Boolean =
        dataStore.data
            .catch { error ->
                if (error is IOException) emit(emptyPreferences()) else throw error
            }
            .first()[SessionRestoreEnabled] ?: false

    override suspend fun setSessionRestoreEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SessionRestoreEnabled] = enabled
        }
    }

    private companion object {
        val SessionRestoreEnabled = booleanPreferencesKey("session_restore_enabled")
    }
}
