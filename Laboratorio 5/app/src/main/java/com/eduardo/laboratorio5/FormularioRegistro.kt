package com.eduardo.laboratorio5

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduardo.laboratorio5.ui.theme.Laboratorio5Theme

private val regexCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

private fun validarNombre(valor: String): String? =
    if (valor.isBlank()) "El nombre es obligatorio" else null

private fun validarCorreo(valor: String): String? = when {
    valor.isBlank() -> "El correo es obligatorio"
    !regexCorreo.matches(valor) -> "El correo no tiene un formato válido"
    else -> null
}

private fun validarTelefono(valor: String): String? = when {
    valor.isBlank() -> "El teléfono es obligatorio"
    valor.any { !it.isDigit() } -> "El teléfono solo admite números"
    valor.length != 9 -> "El teléfono debe tener 9 dígitos"
    else -> null
}

@Composable
fun FormularioRegistro(modifier: Modifier = Modifier) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var nombreTocado by remember { mutableStateOf(false) }
    var correoTocado by remember { mutableStateOf(false) }
    var telefonoTocado by remember { mutableStateOf(false) }

    var guardado by remember { mutableStateOf(false) }

    val errorNombre = validarNombre(nombre)
    val errorCorreo = validarCorreo(correo)
    val errorTelefono = validarTelefono(telefono)

    val esValido = errorNombre == null && errorCorreo == null && errorTelefono == null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Registro",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            isError = nombreTocado && errorNombre != null,
            supportingText = {
                if (nombreTocado && errorNombre != null) Text(errorNombre)
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { if (!it.isFocused) nombreTocado = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            singleLine = true,
            isError = correoTocado && errorCorreo != null,
            supportingText = {
                if (correoTocado && errorCorreo != null) Text(errorCorreo)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { if (!it.isFocused) correoTocado = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            singleLine = true,
            isError = telefonoTocado && errorTelefono != null,
            supportingText = {
                if (telefonoTocado && errorTelefono != null) Text(errorTelefono)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { if (!it.isFocused) telefonoTocado = true }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = {
                    nombre = ""
                    correo = ""
                    telefono = ""
                    nombreTocado = false
                    correoTocado = false
                    telefonoTocado = false
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpiar")
            }
            Button(
                onClick = { guardado = true },
                enabled = esValido,
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }

    if (guardado) {
        AlertDialog(
            onDismissRequest = { guardado = false },
            title = { Text("Datos guardados") },
            text = {
                Text("Nombre: $nombre\nCorreo: $correo\nTeléfono: $telefono")
            },
            confirmButton = {
                TextButton(onClick = { guardado = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormularioRegistroPreview() {
    Laboratorio5Theme {
        FormularioRegistro()
    }
}
