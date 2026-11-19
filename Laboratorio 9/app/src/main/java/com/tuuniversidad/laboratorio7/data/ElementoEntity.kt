package com.tuuniversidad.laboratorio7.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room que representa un Elemento / Personaje de juego persistido en la base de datos SQLite.
 *
 * Atributos de negocio:
 * - id: Clave primaria autogenerada (Int)
 * - nombre: Nombre del elemento/personaje (String)
 * - descripcion: Descripción del rol o habilidad (String)
 * - recursoVisual: Emoji o identificador visual (String)
 * - nivelPoder: Atributo numérico de negocio que indica el nivel de poder del personaje (Int, ej: 1 a 100)
 */
@Entity(tableName = "elementos")
data class ElementoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val recursoVisual: String,
    val nivelPoder: Int = 50
)
