package com.nagarseva.app.data.local.dao

import androidx.room.*
import com.nagarseva.app.data.local.entity.TicketCounterEntity

@Dao
interface TicketCounterDao {

    @Query("""
        SELECT * FROM ticket_counter 
        WHERE date = :date LIMIT 1
    """)
    suspend fun getCounterForDate(
        date: String
    ): TicketCounterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCounter(
        counter: TicketCounterEntity
    )

    // Clean up old counters (older than 30 days)
    @Query("""
        DELETE FROM ticket_counter 
        WHERE date < :cutoffDate
    """)
    suspend fun deleteOldCounters(cutoffDate: String)
}
