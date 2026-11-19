package com.tuuniversidad.laboratorio7.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad ElementoEntity.
 * Proporciona métodos de acceso a datos mediante SQLite administrado por Room:
 * - Consultas reactivas (Flow)
 * - Inserción, actualización y eliminación asíncronas con corrutinas (suspend)
 */
@Dao
interface ElementoDao {

    @Query("SELECT * FROM elementos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ElementoEntity>>

    @Query("SELECT * FROM elementos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): ElementoEntity?

    @Query("SELECT COUNT(*) FROM elementos")
    suspend fun contarElementos(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(elemento: ElementoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(elementos: List<ElementoEntity>)

    @Update
    suspend fun actualizar(elemento: ElementoEntity): Int

    @Delete
    suspend fun eliminar(elemento: ElementoEntity): Int

    @Query("DELETE FROM elementos WHERE id = :id")
    suspend fun eliminarPorId(id: Int): Int
}
