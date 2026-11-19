package com.tuuniversidad.laboratorio7.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuuniversidad.laboratorio7.data.AppDatabase
import com.tuuniversidad.laboratorio7.data.ElementoEntity
import com.tuuniversidad.laboratorio7.data.ElementoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioUiState(
    val idEdicion: Int? = null,
    val nombre: String = "",
    val descripcion: String = "",
    val recursoVisual: String = "🛡️",
    val nivelPoder: String = "50",
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)

class ElementoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ElementoRepository
    val elementos: StateFlow<List<ElementoEntity>>

    private val _formularioState = MutableStateFlow(FormularioUiState())
    val formularioState: StateFlow<FormularioUiState> = _formularioState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ElementoRepository(database.elementoDao())

        elementos = repository.todosLosElementos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Inicializar con los registros base del dominio solo si la BD está vacía (conservando la persistencia)
        viewModelScope.launch {
            repository.insertarInicialesSiEstaVacio(obtenerRegistrosIniciales())
        }
    }

    fun onNombreChange(nuevo: String) {
        _formularioState.update { it.copy(nombre = nuevo, mensajeError = null) }
    }

    fun onDescripcionChange(nuevo: String) {
        _formularioState.update { it.copy(descripcion = nuevo, mensajeError = null) }
    }

    fun onRecursoVisualChange(nuevo: String) {
        _formularioState.update { it.copy(recursoVisual = nuevo) }
    }

    fun onNivelPoderChange(nuevo: String) {
        if (nuevo.all { it.isDigit() } && nuevo.length <= 3) {
            _formularioState.update { it.copy(nivelPoder = nuevo, mensajeError = null) }
        }
    }

    fun cargarParaEdicion(elemento: ElementoEntity) {
        _formularioState.update {
            it.copy(
                idEdicion = elemento.id,
                nombre = elemento.nombre,
                descripcion = elemento.descripcion,
                recursoVisual = elemento.recursoVisual,
                nivelPoder = elemento.nivelPoder.toString(),
                mensajeExito = null,
                mensajeError = null
            )
        }
    }

    fun cancelarEdicion() {
        _formularioState.update {
            FormularioUiState(
                recursoVisual = "🛡️",
                nivelPoder = "50"
            )
        }
    }

    fun guardarOActualizar() {
        val actual = _formularioState.value
        val nombreLimpio = actual.nombre.trim()
        val descLimpia = actual.descripcion.trim()
        val poder = actual.nivelPoder.toIntOrNull()

        if (nombreLimpio.isBlank()) {
            _formularioState.update { it.copy(mensajeError = "El nombre no puede estar vacío.") }
            return
        }
        if (descLimpia.isBlank()) {
            _formularioState.update { it.copy(mensajeError = "La descripción no puede estar vacía.") }
            return
        }
        if (poder == null || poder < 1 || poder > 100) {
            _formularioState.update { it.copy(mensajeError = "El nivel de poder debe estar entre 1 y 100.") }
            return
        }

        viewModelScope.launch {
            try {
                if (actual.idEdicion != null) {
                    val entityActualizada = ElementoEntity(
                        id = actual.idEdicion,
                        nombre = nombreLimpio,
                        descripcion = descLimpia,
                        recursoVisual = actual.recursoVisual,
                        nivelPoder = poder
                    )
                    repository.actualizar(entityActualizada)
                    _formularioState.update {
                        FormularioUiState(
                            recursoVisual = "🛡️",
                            nivelPoder = "50",
                            mensajeExito = "Elemento '${nombreLimpio}' actualizado correctamente en Room."
                        )
                    }
                } else {
                    val nuevoEntity = ElementoEntity(
                        nombre = nombreLimpio,
                        descripcion = descLimpia,
                        recursoVisual = actual.recursoVisual,
                        nivelPoder = poder
                    )
                    repository.insertar(nuevoEntity)
                    _formularioState.update {
                        FormularioUiState(
                            recursoVisual = "🛡️",
                            nivelPoder = "50",
                            mensajeExito = "Elemento '${nombreLimpio}' registrado exitosamente en Room."
                        )
                    }
                }
            } catch (e: Exception) {
                _formularioState.update {
                    it.copy(mensajeError = "Error al interactuar con Room: ${e.localizedMessage}")
                }
            }
        }
    }

    fun eliminar(elemento: ElementoEntity) {
        viewModelScope.launch {
            try {
                repository.eliminar(elemento)
                if (_formularioState.value.idEdicion == elemento.id) {
                    cancelarEdicion()
                }
                _formularioState.update {
                    it.copy(mensajeExito = "Elemento '${elemento.nombre}' eliminado de la base de datos.")
                }
            } catch (e: Exception) {
                _formularioState.update {
                    it.copy(mensajeError = "Error al eliminar: ${e.localizedMessage}")
                }
            }
        }
    }

    fun limpiarMensajeExito() {
        _formularioState.update { it.copy(mensajeExito = null) }
    }

    private fun obtenerRegistrosIniciales(): List<ElementoEntity> {
        return listOf(
            ElementoEntity(nombre = "Guerrero", descripcion = "Experto en combate cuerpo a cuerpo", recursoVisual = "🛡️", nivelPoder = 80),
            ElementoEntity(nombre = "Mago", descripcion = "Lanza hechizos y magia arcana", recursoVisual = "🧙‍♂️", nivelPoder = 90),
            ElementoEntity(nombre = "Arquero", descripcion = "Ataques a distancia precisos", recursoVisual = "🏹", nivelPoder = 75),
            ElementoEntity(nombre = "Sanador", descripcion = "Restaura la salud del equipo", recursoVisual = "💚", nivelPoder = 70),
            ElementoEntity(nombre = "Paladín", descripcion = "Guerrero sagrado con defensa alta", recursoVisual = "⚔️", nivelPoder = 85)
        )
    }
}
