package com.tuuniversidad.laboratorio7

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "preferencias_ui"
)

class PreferenciasRepository(context: Context) {
    private val dataStore = context.preferencesDataStore

    val mostrarDescripciones: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[MOSTRAR_DESCRIPCIONES] ?: MOSTRAR_DESCRIPCIONES_POR_DEFECTO
    }

    suspend fun actualizarMostrarDescripciones(mostrar: Boolean) {
        dataStore.edit { preferences ->
            preferences[MOSTRAR_DESCRIPCIONES] = mostrar
        }
    }

    private companion object {
        val MOSTRAR_DESCRIPCIONES = booleanPreferencesKey("mostrar_descripciones")
        const val MOSTRAR_DESCRIPCIONES_POR_DEFECTO = true
    }
}
