package com.fundacion.enid.campo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * BASE DE DATOS LOCAL — `enid_app_campo.db`
 * ---------------------------------------------------------------------------
 * Es la respuesta directa a la pregunta 1 de la sustentacion: "donde queda
 * fisicamente la informacion cuando no existe Internet?" → en este archivo
 * SQLite, dentro del almacenamiento interno privado de la app, mas las fotos
 * en `filesDir/evidencias/`. Ninguna de las dos depende de la red.
 *
 * No se exporta schema ni se usan migraciones porque la app es v1 del taller;
 * si cambia el modelo se borra la BD en desarrollo (fallbackToDestructiveMigration).
 */
@Database(
    entities = [AspiranteEntity::class],
    version = 1,
    exportSchema = true
)
abstract class EnidDatabase : RoomDatabase() {

    abstract fun AspiranteDao(): AspiranteDao

    companion object {
        const val NOMBRE_BASE_DATOS = "enid_app_campo.db"

        @Volatile
        private var instancia: EnidDatabase? = null

        fun obtener(context: Context): EnidDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    EnidDatabase::class.java,
                    NOMBRE_BASE_DATOS
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instancia = it }
            }
    }
}
