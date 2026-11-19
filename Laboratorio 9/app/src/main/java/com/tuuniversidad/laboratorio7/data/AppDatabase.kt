package com.tuuniversidad.laboratorio7.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos principal de Room para la aplicación.
 * Mantiene una única instancia mediante el patrón Singleton.
 */
@Database(entities = [ElementoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun elementoDao(): ElementoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "laboratorio_room_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
