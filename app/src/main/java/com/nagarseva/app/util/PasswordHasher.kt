package com.nagarseva.app.util

import java.security.MessageDigest

object PasswordHasher {
    
    // Hash password with SHA-256 + salt
    fun hash(password: String): String {
        val salt = "NagarSeva_2025_Salt_Key"
        val input = password + salt
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
        return bytes.joinToString("") { 
            "%02x".format(it) 
        }
    }
    
    // Verify password against stored hash
    fun verify(
        inputPassword: String, 
        storedHash: String
    ): Boolean {
        return hash(inputPassword) == storedHash
    }
    
    // Validate password strength
    fun isStrongEnough(password: String): Boolean {
        return password.length >= 8
    }
    
    // Get strength score 0-4
    fun getStrength(password: String): Int {
        if (password.isEmpty()) return 0
        if (password.length < 6) return 1
        if (password.length < 8) return 2
        val hasNumber = password.any { it.isDigit() }
        val hasSpecial = password.any { 
            !it.isLetterOrDigit() 
        }
        return if (hasNumber && hasSpecial) 4 else 3
    }
}
