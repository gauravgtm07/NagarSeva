package com.nagarseva.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(
    tableName = "reports",
    indices = [
        Index(value = ["ticket_id"], unique = true),
        Index(value = ["user_id"]),
        Index(value = ["status"]),
        Index(value = ["created_at"])
    ]
)
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "ticket_id")
    val ticketId: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "issue_title")
    val issueTitle: String,

    @ColumnInfo(name = "defect_type")
    val defectType: String,

    @ColumnInfo(name = "severity")
    val severity: String = "MEDIUM",

    @ColumnInfo(name = "status")
    val status: String = "SUBMITTED",

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "location_accuracy")
    val locationAccuracy: Float = 0f,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "photo_path")
    val photoPath: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

enum class DefectType(val displayName: String) {
    POTHOLE("Pothole"),
    STREETLIGHT("Street Light"),
    GARBAGE("Garbage"),
    WATERLEAK("Water Leak")
}

enum class ReportStatus(val displayName: String, val color: String) {
    SUBMITTED("Submitted", "#1565C0"),
    IN_REVIEW("In Review", "#FF8F00"),
    RESOLVED("Resolved", "#2E7D32")
}

enum class Severity(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

fun ReportEntity.getStatusEnum(): ReportStatus {
    return try {
        ReportStatus.valueOf(status.uppercase())
    } catch (e: Exception) {
        ReportStatus.SUBMITTED
    }
}

fun ReportEntity.getDefectTypeEnum(): DefectType {
    return try {
        DefectType.valueOf(defectType.uppercase())
    } catch (e: Exception) {
        DefectType.POTHOLE
    }
}

fun ReportEntity.getSeverityEnum(): Severity {
    return try {
        Severity.valueOf(severity.uppercase())
    } catch (e: Exception) {
        Severity.MEDIUM
    }
}

fun ReportEntity.getFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(createdAt))
}

fun ReportEntity.isRecent(): Boolean {
    val currentTime = System.currentTimeMillis()
    val twentyFourHoursInMillis = 24 * 60 * 60 * 1000L
    return (currentTime - createdAt) < twentyFourHoursInMillis
}

fun ReportEntity.getRelativeTime(): String {
    val diff = System.currentTimeMillis() - createdAt
    val minutes = diff / 60000
    val hours = diff / 3600000
    return when {
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        hours < 48 -> "Yesterday"
        else -> getFormattedDate()
    }
}

fun String.getDisplayName(): String = when(this.uppercase()) {
    "POTHOLE" -> "Pothole"
    "STREETLIGHT" -> "Broken Light"
    "GARBAGE" -> "Garbage"
    "WATERLEAK" -> "Water Leak"
    else -> this
}
