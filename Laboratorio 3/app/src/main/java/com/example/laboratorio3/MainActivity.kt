package com.example.laboratorio3

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Rutas {
    const val INICIO = "inicio"
    const val BIENVENIDA = "bienvenida/{nombre}"
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MiAplicacion()
        }
    }
}

@Composable
fun MiAplicacion() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.INICIO
    ) {

        composable(Rutas.INICIO) {
            PantallaInicio(navController)
        }

        composable(Rutas.BIENVENIDA) { backStackEntry ->

            val nombre = backStackEntry
                .arguments
                ?.getString("nombre")
                ?: ""

            PantallaBienvenida(nombre, navController)
        }
    }
}

@Composable
fun PantallaInicio(navController: NavController) {

    var nombre by remember {
        mutableStateOf("")
    }

    var mostrarError by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Pantalla de Inicio",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                mostrarError = false
            },
            label = {
                Text("Ingrese su nombre")
            },
            isError = mostrarError
        )

        if (mostrarError) {

            Text(
                text = "El nombre no puede estar vacío",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {

                if (nombre.isBlank()) {

                    mostrarError = true

                } else {

                    navController.navigate(
                        "bienvenida/${Uri.encode(nombre)}"
                    )
                }
            }
        ) {
            Text("Continuar")
        }
    }
}

@Composable
fun PantallaBienvenida(nombre: String, navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Bienvenido, $nombre",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Volver")
        }
    }
}