package com.tuuniversidad.laboratorio7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {
    private val preferenciasViewModel: PreferenciasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaPrincipal(preferenciasViewModel)
                }
            }
        }
    }
}

// 1. Data class con 4 atributos
data class Elemento(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val recursoVisual: String // Usamos un emoji como recurso visual
)

// 2. Función para generar 20 registros
fun obtenerElementos(): List<Elemento> {
    return listOf(
        Elemento(1, "Guerrero", "Experto en combate cuerpo a cuerpo", "🛡️"),
        Elemento(2, "Mago", "Lanza hechizos y magia arcana", "🧙‍♂️"),
        Elemento(3, "Arquero", "Ataques a distancia precisos", "🏹"),
        Elemento(4, "Sanador", "Restaura la salud del equipo", "💚"),
        Elemento(5, "Ladrón", "Especialista en sigilo y robos", "🦹‍♂️"),
        Elemento(6, "Paladín", "Guerrero sagrado con defensa alta", "⚔️"),
        Elemento(7, "Nigromante", "Invoca a los muertos", "💀"),
        Elemento(8, "Bardo", "Usa la música para apoyar", "🎻"),
        Elemento(9, "Berserker", "Sacrifica defensa por ataque", "🪓"),
        Elemento(10, "Nigromante Oscuro", "Controla almas perdidas", "👻"),
        Elemento(11, "Druida", "Cambia de forma animal", "🐺"),
        Elemento(12, "Monje", "Combate sin armas veloz", "👊"),
        Elemento(13, "Cazador de Dragones", "Especialista en bestias", "🐉"),
        Elemento(14, "Ilusionista", "Crea espejismos", "🎭"),
        Elemento(15, "Alquimista", "Prepara pociones explosivas", "🧪"),
        Elemento(16, "Nigromante", "Comanda ejércitos de huesos", "☠️"),
        Elemento(17, "Valkiria", "Guerrera elegida por los dioses", "🪽"),
        Elemento(18, "Samurái", "Maestro de la espada katana", "🗡️"),
        Elemento(19, "Gladiador", "Luchador de la arena", "🏛️"),
        Elemento(20, "Hechicero", "Magia innata y caótica", "🔮")
    )
}

@Composable
fun PantallaPrincipal(viewModel: PreferenciasViewModel) {
    val elementos = remember { obtenerElementos() }
    var elementoSeleccionado by remember { mutableStateOf<Elemento?>(null) }
    val preferencias by viewModel.uiState.collectAsStateWithLifecycle()

    // Si hay un elemento seleccionado, mostramos el detalle
    if (elementoSeleccionado != null) {
        PantallaDetalle(
            elemento = elementoSeleccionado!!,
            onVolver = { elementoSeleccionado = null }
        )
    } else {
        // Si no, mostramos la lista LazyColumn
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Separación visual
        ) {
            item {
                PreferenciasCard(
                    mostrarDescripciones = preferencias.mostrarDescripciones,
                    error = preferencias.error,
                    onMostrarDescripcionesChange = viewModel::actualizarMostrarDescripciones
                )
            }
            items(elementos, key = { it.id }) { elemento ->
                TarjetaElemento(
                    elemento = elemento,
                    mostrarDescripcion = preferencias.mostrarDescripciones,
                    onClick = { elementoSeleccionado = it }
                )
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
                text = "Preferencias",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mostrar descripciones",
                    modifier = Modifier.weight(1f)
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

// 3. Composable reutilizable
@Composable
fun TarjetaElemento(
    elemento: Elemento,
    mostrarDescripcion: Boolean = true,
    onClick: (Elemento) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(elemento) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Recurso visual
            Text(
                text = elemento.recursoVisual,
                fontSize = 40.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                // Jerarquía tipográfica
                Text(
                    text = elemento.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (mostrarDescripcion) {
                    Text(
                        text = elemento.descripcion,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

// 6. Pantalla de detalle
@Composable
fun PantallaDetalle(elemento: Elemento, onVolver: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = elemento.recursoVisual, fontSize = 100.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = elemento.nombre,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = elemento.descripcion,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onVolver) {
            Text("Volver a la lista")
        }
    }
}