package com.tuuniversidad.laboratorio7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuuniversidad.laboratorio7.data.ElementoEntity
import com.tuuniversidad.laboratorio7.ui.ElementoViewModel

class MainActivity : ComponentActivity() {
    private val preferenciasViewModel: PreferenciasViewModel by viewModels()
    private val elementoViewModel: ElementoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaPrincipal(
                        preferenciasViewModel = preferenciasViewModel,
                        elementoViewModel = elementoViewModel
                    )
                }
            }
        }
    }
}

// 1. Data class de compatibilidad con Guía 07
data class Elemento(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val recursoVisual: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    preferenciasViewModel: PreferenciasViewModel,
    elementoViewModel: ElementoViewModel
) {
    val elementosPersistidos by elementoViewModel.elementos.collectAsStateWithLifecycle()
    val formularioState by elementoViewModel.formularioState.collectAsStateWithLifecycle()
    val preferencias by preferenciasViewModel.uiState.collectAsStateWithLifecycle()

    var elementoSeleccionado by remember { mutableStateOf<ElementoEntity?>(null) }
    var elementoAEliminar by remember { mutableStateOf<ElementoEntity?>(null) }

    // Diálogo de confirmación para eliminar
    if (elementoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { elementoAEliminar = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Deseas eliminar permanentemente a '${elementoAEliminar?.nombre}' de la base de datos Room?") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        elementoAEliminar?.let { elementoViewModel.eliminar(it) }
                        elementoAEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { elementoAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (elementoSeleccionado != null) {
        PantallaDetalle(
            elemento = elementoSeleccionado!!,
            onVolver = { elementoSeleccionado = null }
        )
    } else {
        Scaffold(

        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card de preferencias de UI (mantiene funcionalidad previa de Guía 07/08)
                item {
                    PreferenciasCard(
                        mostrarDescripciones = preferencias.mostrarDescripciones,
                        error = preferencias.error,
                        onMostrarDescripcionesChange = preferenciasViewModel::actualizarMostrarDescripciones
                    )
                }

                // Formulario de Room (Alta y Edición)
                item {
                    FormularioElementoCard(
                        idEdicion = formularioState.idEdicion,
                        nombre = formularioState.nombre,
                        descripcion = formularioState.descripcion,
                        recursoVisual = formularioState.recursoVisual,
                        nivelPoder = formularioState.nivelPoder,
                        mensajeExito = formularioState.mensajeExito,
                        mensajeError = formularioState.mensajeError,
                        onNombreChange = elementoViewModel::onNombreChange,
                        onDescripcionChange = elementoViewModel::onDescripcionChange,
                        onRecursoVisualChange = elementoViewModel::onRecursoVisualChange,
                        onNivelPoderChange = elementoViewModel::onNivelPoderChange,
                        onGuardar = elementoViewModel::guardarOActualizar,
                        onCancelarEdicion = elementoViewModel::cancelarEdicion
                    )
                }

                // Encabezado de la lista reactiva
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Registros en Room (${elementosPersistidos.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Estado vacío si no hay registros
                if (elementosPersistidos.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = "No hay registros en la base de datos Room. Agrega uno usando el formulario superior.",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Items observables desde Room via Flow
                items(elementosPersistidos, key = { it.id }) { elemento ->
                    TarjetaElementoRoom(
                        elemento = elemento,
                        mostrarDescripcion = preferencias.mostrarDescripciones,
                        onClick = { elementoSeleccionado = it },
                        onEditar = { elementoViewModel.cargarParaEdicion(elemento) },
                        onEliminar = { elementoAEliminar = elemento }
                    )
                }
            }
        }
    }
}

@Composable
fun FormularioElementoCard(
    idEdicion: Int?,
    nombre: String,
    descripcion: String,
    recursoVisual: String,
    nivelPoder: String,
    mensajeExito: String?,
    mensajeError: String?,
    onNombreChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onRecursoVisualChange: (String) -> Unit,
    onNivelPoderChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onCancelarEdicion: () -> Unit
) {
    val emojisDisponibles = listOf("🛡️", "🧙‍♂️", "🏹", "💚", "🦹‍♂️", "⚔️", "💀", "🐉", "🔮", "⚡")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (idEdicion == null) "Nuevo Registro (Room)" else "Editar Elemento #$idEdicion",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (idEdicion == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                label = { Text("Nombre del personaje") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = onDescripcionChange,
                label = { Text("Descripción / Rol") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nivelPoder,
                onValueChange = onNivelPoderChange,
                label = { Text("Nivel de Poder (1 - 100)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Selector de emoji / recurso visual
            Text(text = "Ícono / Recurso Visual:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(emojisDisponibles) { emoji ->
                    FilterChip(
                        selected = recursoVisual == emoji,
                        onClick = { onRecursoVisualChange(emoji) },
                        label = { Text(emoji, fontSize = 20.sp) }
                    )
                }
            }

            if (mensajeError != null) {
                Text(
                    text = mensajeError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (mensajeExito != null) {
                Text(
                    text = mensajeExito,
                    color = Color(0xFF2E7D32),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onGuardar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (idEdicion == null) "Guardar en Room" else "Actualizar")
                }

                if (idEdicion != null) {
                    OutlinedButton(
                        onClick = onCancelarEdicion,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaElementoRoom(
    elemento: ElementoEntity,
    mostrarDescripcion: Boolean = true,
    onClick: (ElementoEntity) -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(elemento) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = elemento.recursoVisual,
                fontSize = 38.sp,
                modifier = Modifier.padding(end = 14.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = elemento.nombre,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                        Text("Nvl ${elemento.nivelPoder}", fontSize = 11.sp)
                    }
                }
                if (mostrarDescripcion) {
                    Text(
                        text = elemento.descripcion,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Botones de acción funcional: Editar y Eliminar
            Row {
                IconButton(onClick = onEditar) {
                    Text("✏️", fontSize = 16.sp)
                }
                IconButton(onClick = onEliminar) {
                    Text("🗑️", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun PreferenciasCard(
    mostrarDescripciones: Boolean,
    error: String?,
    onMostrarDescripcionesChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = "Preferencia UI (DataStore)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mostrar descripciones",
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp
                )
                Switch(
                    checked = mostrarDescripciones,
                    onCheckedChange = onMostrarDescripcionesChange,
                    modifier = Modifier.testTag("mostrarDescripcionesSwitch")
                )
            }
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun PantallaDetalle(elemento: ElementoEntity, onVolver: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = elemento.recursoVisual, fontSize = 90.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = elemento.nombre,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
            Text("Nivel de Poder: ${elemento.nivelPoder}", fontSize = 16.sp, modifier = Modifier.padding(6.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = elemento.descripcion,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(onClick = onVolver) {
            Text("Volver al Listado")
        }
    }
}
