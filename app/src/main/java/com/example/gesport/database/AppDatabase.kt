package com.example.gesport.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gesport.models.Facility
import com.example.gesport.models.Reservation
import com.example.gesport.models.Team
import com.example.gesport.models.User
import com.example.gesport.models.Match

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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gessport_db"
                )
                    // Si cambias el schema y no haces migrations todavía, esto evita crash:
                    // .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
