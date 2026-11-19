package com.tuuniversidad.laboratorio7.data

import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que abstrae la fuente de datos para el resto de la aplicación.
 * Conecta el DAO de Room con la capa de presentación (ViewModel).
 */
class ElementoRepository(private val elementoDao: ElementoDao) {

    val todosLosElementos: Flow<List<ElementoEntity>> = elementoDao.obtenerTodos()

    suspend fun contar(): Int = elementoDao.contarElementos()

    suspend fun insertar(elemento: ElementoEntity): Long = elementoDao.insertar(elemento)

    suspend fun insertarInicialesSiEstaVacio(iniciales: List<ElementoEntity>) {
        if (elementoDao.contarElementos() == 0) {
            elementoDao.insertarTodos(iniciales)
        }
    }

    suspend fun actualizar(elemento: ElementoEntity): Int = elementoDao.actualizar(elemento)

    suspend fun eliminar(elemento: ElementoEntity): Int = elementoDao.eliminar(elemento)

    suspend fun eliminarPorId(id: Int): Int = elementoDao.eliminarPorId(id)
}
