package com.nagarseva.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ticket_counter")
data class TicketCounterEntity(
    @PrimaryKey
    val date: String,    // format: "20250428" (YYYYMMDD)

    @ColumnInfo(name = "last_count")
    val lastCount: Int = 0  // increments each report
)
