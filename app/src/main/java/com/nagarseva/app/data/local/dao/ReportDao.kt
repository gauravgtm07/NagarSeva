package com.nagarseva.app.data.local.dao

import androidx.room.*
import com.nagarseva.app.data.local.entity.ReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity): Long

    @Query("SELECT * FROM reports WHERE user_id = :userId ORDER BY created_at DESC")
    fun getReportsByUserFlow(userId: String): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE user_id = :userId AND status = :status ORDER BY created_at DESC")
    fun getReportsByUserAndStatusFlow(userId: String, status: String): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE ticket_id = :ticketId LIMIT 1")
    suspend fun getReportByTicketId(ticketId: String): ReportEntity?

    @Query("SELECT * FROM reports WHERE id = :id LIMIT 1")
    suspend fun getReportById(id: Long): ReportEntity?

    @Query("SELECT * FROM reports WHERE id = :id LIMIT 1")
    fun getReportByIdFlow(id: Long): Flow<ReportEntity?>

    @Query("UPDATE reports SET status = :status, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateReportStatus(id: Long, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteReportById(id: Long)

    @Query("SELECT COUNT(*) FROM reports WHERE user_id = :userId")
    fun getTotalCountFlow(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM reports WHERE user_id = :userId AND status = 'RESOLVED'")
    fun getResolvedCountFlow(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM reports WHERE user_id = :userId AND status != 'RESOLVED'")
    fun getPendingCountFlow(userId: String): Flow<Int>

    @Query("SELECT * FROM reports WHERE user_id = :userId ORDER BY created_at DESC LIMIT 5")
    fun getRecentReportsFlow(userId: String): Flow<List<ReportEntity>>
}
