package com.tuuniversidad.laboratorio7

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PreferenciasUiState(
    val mostrarDescripciones: Boolean = true,
    val error: String? = null
)

class PreferenciasViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PreferenciasRepository(application)
    private val _uiState = MutableStateFlow(PreferenciasUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.mostrarDescripciones
                .catch { exception ->
                    if (exception is IOException) {
                        Log.e(TAG, "No se pudieron leer las preferencias", exception)
                        _uiState.update {
                            it.copy(error = "No se pudo leer la preferencia.")
                        }
                    } else {
                        throw exception
                    }
                }
                .collect { mostrar ->
                    _uiState.update {
                        it.copy(mostrarDescripciones = mostrar, error = null)
                    }
                }
        }
    }

    fun actualizarMostrarDescripciones(mostrar: Boolean) {
        viewModelScope.launch {
            try {
                repository.actualizarMostrarDescripciones(mostrar)
                _uiState.update { it.copy(error = null) }
            } catch (exception: IOException) {
                Log.e(TAG, "No se pudo guardar la preferencia", exception)
                _uiState.update {
                    it.copy(error = "No se pudo guardar la preferencia.")
                }
            }
        }
    }

    private companion object {
        const val TAG = "PreferenciasViewModel"
    }
}
