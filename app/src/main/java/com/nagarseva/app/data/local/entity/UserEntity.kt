package com.nagarseva.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "phone")
    val phone: String = "",

    @ColumnInfo(name = "residential_address")
    val residentialAddress: String = "",

    @ColumnInfo(name = "ward_number")
    val wardNumber: String = "",

    @ColumnInfo(name = "city")
    val city: String = "Bengaluru",

    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun createNew(fullName: String, email: String, passwordHash: String): UserEntity {
            return UserEntity(
                uid = UUID.randomUUID().toString(),
                fullName = fullName,
                email = email,
                passwordHash = passwordHash
            )
        }
    }
}

/**
 * Returns the full name if not blank, otherwise returns the email.
 */
fun UserEntity.toDisplayName(): String {
    return fullName.ifBlank { email }
}

/**
 * Returns initials from the full name (e.g., "Ravi Kumar" -> "RK").
 */
fun UserEntity.getInitials(): String {
    if (fullName.isBlank()) return ""
    val parts = fullName.trim().split("\\s+".toRegex())
    return when {
        parts.size >= 2 -> {
            "${parts.first().first()}${parts.last().first()}".uppercase()
        }
        parts.isNotEmpty() -> {
            parts.first().first().toString().uppercase()
        }
        else -> ""
    }
}
