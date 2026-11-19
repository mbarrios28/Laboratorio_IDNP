#import "@preview/basic-report:0.5.0": *

#show: it => basic-report(
  doc-category: "Informe de laboratorio",
  doc-title: "Persistencia de preferencias con DataStore",
  author: "Estudiante",
  affiliation: "Laboratorio de desarrollo Android",
  language: "es",
  compact-mode: true,
  it
)

= Introducción

Este laboratorio incorpora una preferencia persistente a una aplicación Android existente que presenta un catálogo de personajes. La opción *Mostrar descripciones* modifica la información presentada en las tarjetas y permite observar la diferencia entre un estado conservado en memoria y un valor guardado entre ejecuciones.

La solución utiliza Preferences DataStore como fuente de datos, un repositorio para centralizar la lectura y escritura, un `ViewModel` para exponer el estado reactivo y Jetpack Compose para representar y modificar la preferencia. La plantilla `basic-report` se conserva como base de formato y estructura.

== Contexto técnico

El proyecto utiliza Kotlin 2.2.10, Android Gradle Plugin 9.4.1 y Gradle 9.6.0. La interfaz está implementada con Jetpack Compose y Material 3. La aplicación define `minSdk` 24 y `targetSdk` 37. Para el ciclo de vida se integra `ViewModel` y la recolección de estado con `collectAsStateWithLifecycle`.

= Actividad 1. Incorporación de DataStore

*Objetivo.* Añadir Preferences DataStore y las bibliotecas de ciclo de vida necesarias para conectarlo con Compose.

La dependencia se declaró en el catálogo de versiones y se agregó al módulo `app`. También se incorporaron Lifecycle ViewModel KTX y Lifecycle Runtime Compose para administrar el estado y observarlo respetando el ciclo de vida de la pantalla.

```toml
datastore = "1.1.7"
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }
```

El módulo consume las dependencias mediante los alias `libs.androidx.datastore.preferences`, `libs.androidx.lifecycle.viewmodel.ktx` y `libs.androidx.lifecycle.runtime.compose`. La compilación de `:app:assembleDebug` finalizó correctamente.

* [INDICACIÓN DE CAPTURA — 01: Dependencias]*
Abra `gradle/libs.versions.toml` y `app/build.gradle.kts` en el editor. Muestre la versión de DataStore Preferences y su alias de dependencia, junto con las dependencias añadidas al módulo. La evidencia debe demostrar que la biblioteca está declarada y disponible para la aplicación.

[INSERTAR CAPTURA 01: Catálogo y dependencias de DataStore]

= Actividad 2. Clave tipada y valor predeterminado

*Objetivo.* Definir una preferencia coherente con la interfaz y especificar qué valor se utiliza antes de guardar una selección.

Se seleccionó `Mostrar descripciones`, una opción booleana que decide si cada tarjeta del catálogo presenta su texto descriptivo. La clave se crea con `booleanPreferencesKey("mostrar_descripciones")`. Cuando la clave aún no existe, el repositorio devuelve `true`, por lo que las descripciones aparecen inicialmente.

```kotlin
private companion object {
    val MOSTRAR_DESCRIPCIONES = booleanPreferencesKey("mostrar_descripciones")
    const val MOSTRAR_DESCRIPCIONES_POR_DEFECTO = true
}
```

La preferencia se almacena en el archivo lógico `preferencias_ui`, separado de los estados transitorios de Compose.

* [INDICACIÓN DE CAPTURA — 02: Clave y valor predeterminado]*
Abra `PreferenciasRepository.kt` y centre la captura en la clave tipada, el nombre del archivo DataStore y la expresión que devuelve `MOSTRAR_DESCRIPCIONES_POR_DEFECTO` cuando todavía no existe un valor almacenado. Debe quedar legible tanto la clave como el valor inicial.

[INSERTAR CAPTURA 02: Clave booleana y valor predeterminado]

= Actividad 3. Lectura, escritura y manejo de errores

*Objetivo.* Mantener las operaciones de persistencia fuera de los componentes visuales.

`PreferenciasRepository` obtiene el `DataStore<Preferences>` asociado al contexto de la aplicación. La propiedad `mostrarDescripciones` convierte el flujo de preferencias en un `Flow<Boolean>`, mientras que `actualizarMostrarDescripciones` guarda la nueva selección mediante `edit`. Las funciones suspendidas permiten ejecutar la escritura sin bloquear el hilo de interfaz.

```kotlin
val mostrarDescripciones: Flow<Boolean> = dataStore.data.map { preferences ->
    preferences[MOSTRAR_DESCRIPCIONES] ?: MOSTRAR_DESCRIPCIONES_POR_DEFECTO
}

suspend fun actualizarMostrarDescripciones(mostrar: Boolean) {
    dataStore.edit { preferences ->
        preferences[MOSTRAR_DESCRIPCIONES] = mostrar
    }
}
```

El `ViewModel` captura errores de E/S tanto durante la lectura como durante la escritura, los registra con `Log.e` y expone un mensaje para la interfaz. Las excepciones que no son `IOException` no se ocultan: se vuelven a lanzar.

* [INDICACIÓN DE CAPTURA — 03: Repositorio y excepciones]*
Abra `PreferenciasRepository.kt` y `PreferenciasViewModel.kt`. Muestre la lectura y escritura centralizadas, así como el tratamiento específico de `IOException` y el registro del error. La evidencia debe permitir distinguir esta lógica de los composables.

[INSERTAR CAPTURA 03: Repositorio y manejo explícito de errores]

= Actividad 4. Estado reactivo con ViewModel y Flow

*Objetivo.* Comunicar los cambios persistidos a la interfaz de forma reactiva.

El `ViewModel` recolecta el `Flow<Boolean>` en `viewModelScope` y publica `PreferenciasUiState` mediante un `StateFlow` de solo lectura. Compose observa el estado con `collectAsStateWithLifecycle`; así, cuando DataStore emite un valor actualizado, la interfaz se recompone mientras la pantalla está activa.

```kotlin
val uiState = _uiState.asStateFlow()

repository.mostrarDescripciones.collect { mostrar ->
    _uiState.update { it.copy(mostrarDescripciones = mostrar, error = null) }
}
```

La relación entre capas queda organizada de la siguiente manera: DataStore conserva el valor, el repositorio transforma y actualiza las preferencias, el `ViewModel` administra el estado de pantalla y Compose lo presenta.

* [INDICACIÓN DE CAPTURA — 04: Estado observable]*
Abra `PreferenciasViewModel.kt` y `MainActivity.kt`. En una misma vista o en capturas consecutivas muestre el `StateFlow`, la recolección en `viewModelScope` y `collectAsStateWithLifecycle`. Deben distinguirse la publicación del estado y su observación desde Compose.

[INSERTAR CAPTURA 04: Integración reactiva entre ViewModel y Compose]

= Actividad 5. Control para modificar la preferencia

*Objetivo.* Permitir que la persona cambie la opción desde la pantalla ya existente.

Se agregó una tarjeta de preferencias al inicio de la lista, con un `Switch` identificado como *Mostrar descripciones*. Su valor se enlaza al estado observado y su evento llama al `ViewModel`. Cuando la opción está desactivada, las tarjetas omiten las descripciones; la lista y la navegación a los detalles existentes se mantienen.

```kotlin
Switch(
    checked = mostrarDescripciones,
    onCheckedChange = onMostrarDescripcionesChange
)
```

La prueba instrumentada confirmó que al desactivar el control desaparece el texto descriptivo del primer personaje. El repositorio, consultado nuevamente desde la prueba, leyó el nuevo valor `false`.

* [INDICACIÓN DE CAPTURA — 05: Control de preferencias]*
Ejecute la aplicación y deje visible la parte superior del catálogo. La captura debe incluir la tarjeta *Preferencias*, la etiqueta completa y el `Switch` en la posición que se desea evidenciar. Para mostrar el cambio, desactive la opción y asegúrese de que una tarjeta ya no presente su descripción.

[INSERTAR CAPTURA 05: Interfaz con la preferencia modificada]

= Actividad 6. Cierre y nuevo inicio de la aplicación

*Objetivo.* Comprobar la conservación de la elección después de detener el proceso, no solo después de una recomposición.

En el dispositivo conectado se detuvo el proceso de la aplicación y se solicitó un inicio en frío (`LaunchState: COLD`). Sin embargo, después de la prueba instrumentada el dispositivo no permitió inspeccionar el árbol de interfaz en ese inicio. Por esta razón no se declara confirmado visualmente el valor mostrado tras el reinicio; esta comprobación queda pendiente de una verificación manual en pantalla.

La lectura de DataStore y la recreación de la Activity se comprobaron mediante la prueba instrumentada, pero una recreación no sustituye la verificación visual tras detener y volver a iniciar completamente la aplicación.

* [INDICACIÓN DE CAPTURA — 06: Persistencia después del reinicio]*
Abra la aplicación, compruebe el estado actual del `Switch` y cierre completamente la aplicación desde la vista de aplicaciones recientes o con una detención completa del proceso. Iníciela de nuevo desde el lanzador. Tome la captura únicamente cuando la pantalla vuelva a mostrar el catálogo. Deben verse la preferencia seleccionada y, de acuerdo con su estado, las descripciones visibles u ocultas; esta evidencia debe demostrar que el valor sobrevivió al cierre.

[INSERTAR CAPTURA 06: Aplicación reiniciada con la preferencia conservada]

= Actividad 7. Validación de escenarios

*Objetivo.* Registrar por separado el valor inicial, el cambio realizado y el resultado del reinicio, sin atribuir resultados a pruebas no verificadas.

El valor inicial `true` y las descripciones visibles se observaron en la pantalla después de instalar la implementación. La prueba instrumentada `preferenciaActivadaActualizaUIYSeAlmacenaTrasRecrearActivity` confirmó el cambio a `false`, la actualización de la interfaz y la lectura del valor desde una nueva consulta al repositorio. La prueba también recreó la Activity y verificó nuevamente el estado. Esta recreación no se contabiliza como reinicio completo.

#table(
  columns: (1.1fr, 2fr, 1.8fr, 2.3fr),
  [*Prueba*], [*Procedimiento*], [*Resultado esperado*], [*Resultado obtenido*],
  [Valor inicial], [Primera ejecución con la clave aún no guardada], [Control activado y descripciones visibles], [Verificado en la interfaz: control activado y descripciones visibles.],
  [Cambio de preferencia], [Desactivar el `Switch` desde el catálogo], [La interfaz cambia y DataStore emite `false`], [Verificado con prueba instrumentada: control desactivado, descripción oculta y valor `false` leído desde el repositorio.],
  [Reinicio], [Detener completamente el proceso y volver a iniciar la aplicación], [Se conserva la selección anterior], [Pendiente: se inició una Activity en frío, pero no se pudo inspeccionar la interfaz después del inicio. Falta evidencia visual del valor conservado.]
)

* [INDICACIÓN DE CAPTURA — 07: Valor inicial y cambio]*
Para el valor inicial, abra la pantalla tras eliminar los datos de la aplicación o en una instalación limpia, y muestre el control activado junto con la descripción de una tarjeta. Para el cambio, desactive el `Switch` y muestre en la misma captura el control desactivado y una tarjeta sin descripción. Estas evidencias deben corresponder a estados observados en el dispositivo.

[INSERTAR CAPTURA 07: Valor inicial y respuesta al cambio]

= Actividad 8. Estado temporal y dato persistente

*Objetivo.* Diferenciar los datos de interfaz mantenidos en memoria de las preferencias almacenadas entre ejecuciones.

Un estado temporal, como `elementoSeleccionado` declarado con `remember` en Compose, existe en memoria para controlar la interfaz durante su ciclo de vida. Puede restablecerse al destruirse la Activity o el proceso, y no está pensado para recuperar una preferencia después de cerrar la aplicación.

Un dato persistente se escribe en almacenamiento local. En esta implementación, `mostrar_descripciones` se guarda mediante DataStore. Al iniciar de nuevo, el repositorio emite el valor guardado o utiliza `true` si aún no hay una selección. De este modo, la preferencia no depende exclusivamente de una variable local.

`Flow` informa las lecturas y actualizaciones; el `ViewModel` lo transforma en un `StateFlow` apto para la pantalla; y Compose recolecta ese estado de forma consciente del ciclo de vida. Esta separación evita duplicar la persistencia en los composables y permite que la interfaz refleje los cambios sin reiniciar la aplicación. La verificación adicional después de un inicio en frío es necesaria para demostrar que el valor persiste entre ejecuciones, no solo durante una recomposición o recreación de Activity.

= Conclusiones

- Preferences DataStore permitió almacenar la opción de visualización con una clave tipada y un valor inicial explícito. La separación en un repositorio concentró las operaciones de persistencia fuera de la interfaz.
- El estado mantenido únicamente en memoria puede desaparecer al terminar el proceso, mientras que la preferencia guardada en DataStore se puede volver a leer. Esta diferencia hace posible conservar la configuración elegida y evita que la experiencia del usuario dependa de valores reinicializados al abrir la aplicación.
- La combinación de DataStore, `Flow`, `StateFlow`, `ViewModel` y Compose reflejó el cambio en la pantalla, y la prueba instrumentada confirmó la escritura y la lectura del nuevo valor. La comprobación visual tras el cierre completo sigue pendiente y debe realizarse para concluir la validación del reinicio.
