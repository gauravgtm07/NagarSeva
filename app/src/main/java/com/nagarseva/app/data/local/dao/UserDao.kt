package com.nagarseva.app.data.local.dao

import androidx.room.*
import com.nagarseva.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    // INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long
    
    // READ - single user operations
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getUserById(uid: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    fun getUserByIdFlow(uid: String): Flow<UserEntity?>
    
    // CHECK if email already exists
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun countByEmail(email: String): Int
    
    // UPDATE
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Query("""
        UPDATE users SET 
        full_name = :fullName,
        phone = :phone,
        residential_address = :address,
        updated_at = :updatedAt
        WHERE uid = :uid
    """)
    suspend fun updateUserProfile(
        uid: String,
        fullName: String,
        phone: String,
        address: String,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE users SET 
        password_hash = :newHash,
        updated_at = :updatedAt
        WHERE uid = :uid
    """)
    suspend fun updatePassword(
        uid: String,
        newHash: String,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    // DELETE
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun deleteUserById(uid: String)
    
    // VERIFY credentials (for login check)
    @Query("""
        SELECT * FROM users 
        WHERE email = :email 
        AND password_hash = :passwordHash 
        LIMIT 1
    """)
    suspend fun verifyCredentials(
        email: String,
        passwordHash: String
    ): UserEntity?
}
