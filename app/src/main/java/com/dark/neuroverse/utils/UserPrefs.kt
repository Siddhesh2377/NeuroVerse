package com.dark.neuroverse.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object UserPrefs {
    private val TERMS_ACCEPTED_KEY = booleanPreferencesKey("terms_accepted")

    fun isTermsAccepted(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { it[TERMS_ACCEPTED_KEY] == true }
    }

    suspend fun setTermsAccepted(context: Context, accepted: Boolean) {
        context.dataStore.edit { it[TERMS_ACCEPTED_KEY] = accepted }
    }
}
