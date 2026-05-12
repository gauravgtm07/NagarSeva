package com.nagarseva.app.di

import android.content.Context
import com.nagarseva.app.data.local.dao.ReportDao
import com.nagarseva.app.data.local.dao.TicketCounterDao
import com.nagarseva.app.data.local.dao.UserDao
import com.nagarseva.app.data.repository.AuthRepository
import com.nagarseva.app.data.repository.ReportRepository
import com.nagarseva.app.util.SessionManager
import com.nagarseva.app.util.ThemeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideSessionManager(
        @ApplicationContext context: Context
    ): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideThemeManager(
        @ApplicationContext context: Context
    ): ThemeManager {
        return ThemeManager(context)
    }
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        userDao: UserDao,
        sessionManager: SessionManager
    ): AuthRepository {
        return AuthRepository(userDao, sessionManager)
    }
    
    @Provides
    @Singleton
    fun provideReportRepository(
        reportDao: ReportDao,
        ticketCounterDao: TicketCounterDao,
        sessionManager: SessionManager
    ): ReportRepository {
        return ReportRepository(
            reportDao, 
            ticketCounterDao, 
            sessionManager
        )
    }
}
