package com.nagarseva.app.di

import android.content.Context
import androidx.room.Room
import com.nagarseva.app.data.local.AppDatabase
import com.nagarseva.app.data.local.dao.ReportDao
import com.nagarseva.app.data.local.dao.TicketCounterDao
import com.nagarseva.app.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        // Run DB operations on IO thread
        .setQueryCallback(
            { sqlQuery, bindArgs ->
                // Logging in debug only
            },
            Executors.newSingleThreadExecutor()
        )
        .build()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(
        database: AppDatabase
    ): UserDao = database.userDao()
    
    @Provides
    @Singleton
    fun provideReportDao(
        database: AppDatabase
    ): ReportDao = database.reportDao()
    
    @Provides
    @Singleton
    fun provideTicketCounterDao(
        database: AppDatabase
    ): TicketCounterDao = database.ticketCounterDao()
}
