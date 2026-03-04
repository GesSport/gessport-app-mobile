package com.example.gesport.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gesport.models.Facility
import com.example.gesport.models.Match
import com.example.gesport.models.Reservation
import com.example.gesport.models.Team
import com.example.gesport.models.User
import com.example.gesport.models.UserRoles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Team::class,
        Match::class,
        Facility::class,
        Reservation::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun teamDao(): TeamDao
    abstract fun matchDao(): MatchDao
    abstract fun facilityDao(): FacilityDao
    abstract fun reservationDao(): ReservationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Credenciales seed (para poder entrar siempre si la BD se resetea)
        private const val SEED_ADMIN_EMAIL = "admin@gesport.com"
        private const val SEED_ADMIN_PASSWORD = "Admin1234"
        private const val SEED_ADMIN_NAME = "Administrador"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gessport_db"
                )
                    // Si cambias el schema y no haces migrations todavía, esto evita crash:
                    //.fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                // Asegura que siempre exista 1 admin
                CoroutineScope(Dispatchers.IO).launch {
                    ensureSeedAdmin(instance)
                }

                instance
            }
        }

        private suspend fun ensureSeedAdmin(db: AppDatabase) {
            val userDao = db.userDao()

            // Si ya existe un admin, no hacemos nada
            val anyAdmin = userDao.getAnyAdmin()
            if (anyAdmin != null) return

            // Si no hay admin: creamos uno fijo
            val seed = User(
                nombre = SEED_ADMIN_NAME,
                email = SEED_ADMIN_EMAIL,
                password = SEED_ADMIN_PASSWORD,
                rol = UserRoles.ADMIN_DEPORTIVO
            )

            userDao.insertUser(seed)
        }
    }
}