package com.nagarseva.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nagarseva.app.data.local.dao.ReportDao
import com.nagarseva.app.data.local.dao.TicketCounterDao
import com.nagarseva.app.data.local.dao.UserDao
import com.nagarseva.app.data.local.entity.ReportEntity
import com.nagarseva.app.data.local.entity.TicketCounterEntity
import com.nagarseva.app.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ReportEntity::class,
        TicketCounterEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun reportDao(): ReportDao
    abstract fun ticketCounterDao(): TicketCounterDao

    companion object {
        const val DATABASE_NAME = "nagarseva_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
