package com.nagarseva.app.data.repository

import com.nagarseva.app.data.local.dao.UserDao
import com.nagarseva.app.data.local.entity.UserEntity
import com.nagarseva.app.util.PasswordHasher
import com.nagarseva.app.util.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {
    
    // REGISTER new user
    suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): AuthResult<UserEntity> {
        return try {
            // a) Validates fullName not blank
            if (fullName.isBlank()) {
                return AuthResult.Error("Full name cannot be empty")
            }
            // b) Validates email format
            if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return AuthResult.Error("Please enter a valid email")
            }
            // c) Validates password length >= 8
            if (password.length < 8) {
                return AuthResult.Error("Password must be at least 8 characters")
            }
            
            // d) Checks email not already in DB: userDao.countByEmail(email) == 0
            if (userDao.countByEmail(email.trim().lowercase()) > 0) {
                return AuthResult.Error("This email is already registered.")
            }
            
            // e) Hashes password: PasswordHasher.hash(password)
            val passwordHash = PasswordHasher.hash(password)
            
            // f) Creates UserEntity with UUID: UserEntity.createNew(fullName, email, hash)
            val newUser = UserEntity.createNew(
                fullName = fullName.trim(),
                email = email.trim().lowercase(),
                passwordHash = passwordHash
            )
            
            // g) Inserts to Room: userDao.insertUser(user)
            userDao.insertUser(newUser)
            
            // h) Saves session: sessionManager.saveSession(userId, email, fullName)
            sessionManager.saveSession(
                userId = newUser.uid,
                email = newUser.email,
                fullName = newUser.fullName
            )
            
            // i) Returns AuthResult.Success(user)
            AuthResult.Success(newUser)
            
        } catch (e: Exception) {
            AuthResult.Error("Registration failed: ${e.message}")
        }
    }
    
    // LOGIN existing user
    suspend fun login(
        email: String,
        password: String
    ): AuthResult<UserEntity> {
        return try {
            // a) Validates email not blank
            if (email.isBlank()) {
                return AuthResult.Error("Please enter your email")
            }
            // b) Validates password not blank
            if (password.isBlank()) {
                return AuthResult.Error("Please enter your password")
            }
            
            // c) Finds user: userDao.getUserByEmail(email)
            val user = userDao.getUserByEmail(email.trim().lowercase())
            
            // d) If null: returns error "No account found"
            if (user == null) {
                return AuthResult.Error("No account found")
            }
            
            // e) Verifies password: PasswordHasher.verify(inputPassword, user.passwordHash)
            val isPasswordCorrect = PasswordHasher.verify(password, user.passwordHash)
            
            // f) If wrong: returns error "Incorrect password"
            if (!isPasswordCorrect) {
                return AuthResult.Error("Incorrect password")
            }
            
            // g) Saves session: sessionManager.saveSession(userId, email, fullName)
            sessionManager.saveSession(
                userId = user.uid,
                email = user.email,
                fullName = user.fullName
            )
            
            // h) Returns AuthResult.Success(user)
            AuthResult.Success(user)
            
        } catch (e: Exception) {
            AuthResult.Error("Login failed: ${e.message}")
        }
    }
    
    // LOGOUT
    suspend fun logout() {
        sessionManager.clearSession()
    }
    
    // RESET PASSWORD
    suspend fun resetPassword(
        currentPassword: String,
        newPassword: String
    ): AuthResult<Unit> {
        return try {
            val userId = sessionManager.getUserId()
                ?: return AuthResult.Error("Session expired. Please login again.")
            
            val user = userDao.getUserById(userId)
                ?: return AuthResult.Error("User not found.")
            
            if (!PasswordHasher.verify(currentPassword, user.passwordHash)) {
                return AuthResult.Error("Current password is incorrect.")
            }
            
            if (newPassword.length < 8) {
                return AuthResult.Error("New password must be at least 8 characters.")
            }
            
            val newHash = PasswordHasher.hash(newPassword)
            userDao.updatePassword(userId, newHash)
            
            AuthResult.Success(Unit)
            
        } catch (e: Exception) {
            AuthResult.Error("Password reset failed: ${e.message}")
        }
    }
    
    // GET current user data
    suspend fun getCurrentUser(): UserEntity? {
        val userId = sessionManager.getUserId() 
            ?: return null
        return userDao.getUserById(userId)
    }
    
    // GET current user as Flow
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCurrentUserFlow(): Flow<UserEntity?> {
        return sessionManager.userIdFlow.flatMapLatest { userId ->
            if (userId != null) {
                userDao.getUserByIdFlow(userId)
            } else {
                flowOf(null)
            }
        }
    }
    
    // UPDATE profile
    suspend fun updateProfile(
        fullName: String,
        phone: String,
        address: String
    ): AuthResult<Unit> {
        return try {
            val userId = sessionManager.getUserId()
                ?: return AuthResult.Error("Session expired.")
            
            userDao.updateUserProfile(
                uid = userId,
                fullName = fullName.trim(),
                phone = phone.trim(),
                address = address.trim()
            )
            
            sessionManager.updateUserName(fullName.trim())
            
            AuthResult.Success(Unit)
            
        } catch (e: Exception) {
            AuthResult.Error("Profile update failed: ${e.message}")
        }
    }
}
