package com.nagarseva.app.util

import com.nagarseva.app.data.local.dao.TicketCounterDao
import com.nagarseva.app.data.local.entity.TicketCounterEntity
import java.text.SimpleDateFormat
import java.util.*

object TicketIdGenerator {

    /**
     * Generates a sequential ticket ID using the local database.
     * Format: NS-YYYYMMDD-XXXX (e.g. NS-20250428-0001)
     */
    suspend fun generateWithDb(ticketCounterDao: TicketCounterDao): String {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        // Get or create counter for today
        val counter = ticketCounterDao.getCounterForDate(today)

        val newCount = (counter?.lastCount ?: 0) + 1

        // Save updated counter
        ticketCounterDao.upsertCounter(
            TicketCounterEntity(
                date = today,
                lastCount = newCount
            )
        )

        // Format: NS-20250428-0042
        return "NS-$today-${newCount.toString().padStart(4, '0')}"
    }

    /**
     * Fallback: UUID based generation if database is not reachable or for unsaved reports.
     */
    fun generateFallback(): String {
        val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val random = (1000..9999).random()
        return "NS-$dateStr-$random"
    }
}
