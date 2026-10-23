# Laboratorio 5 — Formulario con Row, Column y validación de estado

**Asignatura:** Introducción al Desarrollo de Nuevas Plataformas
**Año:** 2026 - B
**Práctica:** N° 05

## Descripción

Se construyó un formulario de registro con tres campos: Nombre, Correo y Teléfono. Los campos están organizados de forma vertical con `Column` y los botones de acción en una fila con `Row`. Cada campo guarda su información de forma independiente y se revisa antes de permitir guardar.

## Código relevante

### 1. Estado independiente por campo

```kotlin
var nombre by remember { mutableStateOf("") }
var correo by remember { mutableStateOf("") }
var telefono by remember { mutableStateOf("") }
```

### 2. Organización vertical con Column

```kotlin
Column(
    modifier = modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
) {
    // OutlinedTextField de Nombre, Correo y Teléfono
}
```

### 3. Reglas de validación

```kotlin
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
```

### 4. Error visible en el campo

```kotlin
isError = nombreTocado && errorNombre != null,
supportingText = {
    if (nombreTocado && errorNombre != null) Text(errorNombre)
}
```

### 5. Botones en Row y habilitación de Guardar

```kotlin
val esValido = errorNombre == null && errorCorreo == null && errorTelefono == null

Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(16.dp)
) {
    OutlinedButton(onClick = { /* limpiar campos y errores */ }) {
        Text("Limpiar")
    }
    Button(onClick = { guardado = true }, enabled = esValido) {
        Text("Guardar")
    }
}
```

### 6. Confirmación al guardar

```kotlin
if (guardado) {
    AlertDialog(
        onDismissRequest = { guardado = false },
        title = { Text("Datos guardados") },
        text = { Text("Nombre: $nombre\nCorreo: $correo\nTeléfono: $telefono") },
        confirmButton = {
            TextButton(onClick = { guardado = false }) { Text("Aceptar") }
        }
    )
}
```

## Casos de prueba

| Caso | Nombre | Correo | Teléfono | Resultado esperado |
|------|--------|--------|----------|--------------------|
| 1 | (vacío) | (vacío) | (vacío) | Error "obligatorio" en los tres campos. Botón Guardar deshabilitado. |
| 2 | Eduardo | eduardo@correo | (vacío) | Error "El teléfono es obligatorio". Guardar deshabilitado. |
| 3 | Eduardo | eduardo@correo | 123abc45 | Error "El teléfono solo admite números". Guardar deshabilitado. |
| 4 | Eduardo | eduardo | 987654321 | Error "El correo no tiene un formato válido". Guardar deshabilitado. |
| 5 | Eduardo | eduardo@correo.com | 987654321 | Sin errores. Guardar habilitado. Al presionar, muestra la confirmación. |
| 6 | Datos del caso 5, luego Limpiar | — | — | Los tres campos quedan vacíos y se quitan los mensajes de error. |

Resultado: los datos inválidos se detectan, se muestra el mensaje correspondiente y no se puede guardar. Los datos válidos permiten guardar. El botón Limpiar borra todo.

## Cuestionario de consolidación

1. **¿Qué ventaja tiene validar antes de almacenar o enviar información?**
   Evita guardar datos incompletos o con errores. Así se reduce el trabajo de corregir después y se mantiene la información con una calidad mínima desde el inicio.

2. **¿Cuándo resulta conveniente usar Row dentro de Column?**
   Cuando se necesita colocar varios elementos uno al lado del otro, como los botones de acción, dentro de una pantalla que en general se lee de arriba hacia abajo.

3. **¿Qué diferencia existe entre guardar un valor solo en estado y persistirlo?**
   El estado guarda el valor mientras la pantalla está abierta; al cerrar la aplicación se pierde. Persistir significa almacenarlo en un lugar que se mantiene, como un archivo o una base de datos, para recuperarlo después.

## Evidencias

- Captura del formulario con errores: `mockup-incremento2.html`
- Captura del formulario válido y confirmación: `mockup-incremento3.html`
- Código de la pantalla: `app/src/main/java/com/eduardo/laboratorio5/FormularioRegistro.kt`
