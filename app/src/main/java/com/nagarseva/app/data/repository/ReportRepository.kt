package com.nagarseva.app.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.nagarseva.app.data.local.dao.ReportDao
import com.nagarseva.app.data.local.dao.TicketCounterDao
import com.nagarseva.app.data.local.entity.ReportEntity
import com.nagarseva.app.util.SessionManager
import com.nagarseva.app.util.TicketIdGenerator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val reportDao: ReportDao,
    private val ticketCounterDao: TicketCounterDao,
    private val sessionManager: SessionManager
) {
    
    // SUBMIT new report — saves to Room DB
    suspend fun submitReport(
        issueTitle: String,
        defectType: String,
        severity: String = "MEDIUM",
        latitude: Double,
        longitude: Double,
        locationAccuracy: Float = 0f,
        address: String,
        photoPath: String,
        description: String = ""
    ): AuthResult<ReportEntity> {
        return try {
            // 1. Get current user ID
            val userId = sessionManager.getUserId()
                ?: return AuthResult.Error(
                    "Please login to submit a report.")
            
            // 2. Generate unique ticket ID
            val ticketId = try {
                TicketIdGenerator
                    .generateWithDb(ticketCounterDao)
            } catch (e: Exception) {
                // Fallback to UUID-based ID
                TicketIdGenerator.generateFallback()
            }
            
            // 3. Create report entity
            val report = ReportEntity(
                ticketId = ticketId,
                userId = userId,
                issueTitle = issueTitle.trim(),
                defectType = defectType,
                severity = severity,
                status = "SUBMITTED",
                latitude = latitude,
                longitude = longitude,
                locationAccuracy = locationAccuracy,
                address = address,
                photoPath = photoPath,
                description = description.trim()
            )
            
            // 4. Insert into Room DB
            val insertedId = reportDao.insertReport(report)
            
            if (insertedId > 0) {
                AuthResult.Success(
                    report.copy(id = insertedId))
            } else {
                AuthResult.Error(
                    "Failed to save report. Try again.")
            }
            
        } catch (e: SQLiteConstraintException) {
            // Ticket ID collision — retry with fallback
            AuthResult.Error(
                "Duplicate ticket ID. Please try again.")
        } catch (e: Exception) {
            AuthResult.Error(
                "Report submission failed: ${e.message}")
        }
    }
    
    // GET all reports for current user (reactive Flow)
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserReportsFlow(): Flow<List<ReportEntity>> {
        return sessionManager.userIdFlow.flatMapLatest { 
            userId ->
            if (userId != null) {
                reportDao.getReportsByUserFlow(userId)
            } else {
                flowOf(emptyList())
            }
        }
    }
    
    // GET filtered reports
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFilteredReportsFlow(
        status: String?
    ): Flow<List<ReportEntity>> {
        return sessionManager.userIdFlow.flatMapLatest { 
            userId ->
            if (userId == null) return@flatMapLatest flowOf(emptyList())
            
            if (status == null || status == "ALL") {
                reportDao.getReportsByUserFlow(userId)
            } else {
                reportDao.getReportsByUserAndStatusFlow(
                    userId, status)
            }
        }
    }
    
    // TRACK by ticket ID
    suspend fun trackReport(
        ticketId: String
    ): AuthResult<ReportEntity> {
        return try {
            if (ticketId.isBlank()) {
                return AuthResult.Error(
                    "Please enter a Ticket ID.")
            }
            
            val report = reportDao
                .getReportByTicketId(ticketId.trim().uppercase())
            
            if (report != null) {
                AuthResult.Success(report)
            } else {
                AuthResult.Error(
                    "No report found with Ticket ID: " +
                    "$ticketId\n\n" +
                    "Please check the ID and try again.")
            }
            
        } catch (e: Exception) {
            AuthResult.Error(
                "Search failed: ${e.message}")
        }
    }
    
    // GET report by ID (for detail screen)
    suspend fun getReportById(
        id: Long
    ): AuthResult<ReportEntity> {
        return try {
            val report = reportDao.getReportById(id)
            if (report != null) {
                AuthResult.Success(report)
            } else {
                AuthResult.Error("Report not found.")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error")
        }
    }
    
    // GET report by ID as Flow
    fun getReportByIdFlow(id: Long): Flow<ReportEntity?> {
        return reportDao.getReportByIdFlow(id)
    }
    
    // GET stats for home screen
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserStatsFlow(): Flow<Triple<Int, Int, Int>> {
        return sessionManager.userIdFlow.flatMapLatest { 
            userId ->
            if (userId == null) {
                return@flatMapLatest flowOf(
                    Triple(0, 0, 0))
            }
            combine(
                reportDao.getTotalCountFlow(userId),
                reportDao.getResolvedCountFlow(userId),
                reportDao.getPendingCountFlow(userId)
            ) { total, resolved, pending ->
                Triple(total, resolved, pending)
            }
        }
    }
    
    // DELETE report
    suspend fun deleteReport(
        reportId: Long
    ): AuthResult<Unit> {
        return try {
            reportDao.deleteReportById(reportId)
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(
                "Delete failed: ${e.message}")
        }
    }
    
    // GET recent activity for home screen
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getRecentActivityFlow(): Flow<List<ReportEntity>> {
        return sessionManager.userIdFlow.flatMapLatest { 
            userId ->
            if (userId != null) {
                reportDao.getRecentReportsFlow(userId)
            } else {
                flowOf(emptyList())
            }
        }
    }
}
