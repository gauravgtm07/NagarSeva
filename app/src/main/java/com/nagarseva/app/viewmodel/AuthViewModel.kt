package com.nagarseva.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nagarseva.app.data.local.entity.UserEntity
import com.nagarseva.app.data.repository.AuthRepository
import com.nagarseva.app.data.repository.AuthResult
import com.nagarseva.app.util.SessionManager
import com.nagarseva.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    // ── Login State ──────────────────────
    private val _loginState = MutableStateFlow<UiState<UserEntity>>(UiState.Idle)
    val loginState: StateFlow<UiState<UserEntity>> = _loginState.asStateFlow()
    
    // ── Register State ───────────────────
    private val _registerState = MutableStateFlow<UiState<UserEntity>>(UiState.Idle)
    val registerState: StateFlow<UiState<UserEntity>> = _registerState.asStateFlow()
    
    // ── Reset Password State ─────────────
    private val _resetState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val resetState: StateFlow<UiState<Unit>> = _resetState.asStateFlow()
    
    // ── Current User State ───────────────
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()
    
    // ── Update Profile State ─────────────
    private val _updateProfileState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val updateProfileState: StateFlow<UiState<Unit>> = _updateProfileState.asStateFlow()
    
    init {
        // Load current user on ViewModel creation
        loadCurrentUser()
    }
    
    // LOGIN function
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            
            // Small delay to show loading (UX)
            delay(500)
            
            when (val result = authRepository.login(email, password)) {
                is AuthResult.Success -> {
                    _currentUser.value = result.data
                    _loginState.value = UiState.Success(result.data)
                }
                is AuthResult.Error -> {
                    _loginState.value = UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }
    
    // REGISTER function
    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            // Client-side validation first
            if (password != confirmPassword) {
                _registerState.value = UiState.Error("Passwords do not match.")
                return@launch
            }
            
            _registerState.value = UiState.Loading
            delay(500)
            
            when (val result = authRepository.register(fullName, email, password)) {
                is AuthResult.Success -> {
                    _currentUser.value = result.data
                    _registerState.value = UiState.Success(result.data)
                }
                is AuthResult.Error -> {
                    _registerState.value = UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }
    
    // RESET PASSWORD function
    fun resetPassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ) {
        viewModelScope.launch {
            if (newPassword != confirmNewPassword) {
                _resetState.value = UiState.Error("New passwords do not match.")
                return@launch
            }
            
            _resetState.value = UiState.Loading
            
            when (val result = authRepository.resetPassword(currentPassword, newPassword)) {
                is AuthResult.Success -> {
                    _resetState.value = UiState.Success(Unit)
                }
                is AuthResult.Error -> {
                    _resetState.value = UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }
    
    // LOGOUT function
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _currentUser.value = null
            _loginState.value = UiState.Idle
        }
    }
    
    // UPDATE PROFILE
    fun updateProfile(
        fullName: String,
        phone: String,
        address: String
    ) {
        viewModelScope.launch {
            _updateProfileState.value = UiState.Loading
            
            when (val result = authRepository.updateProfile(fullName, phone, address)) {
                is AuthResult.Success -> {
                    loadCurrentUser() // refresh
                    _updateProfileState.value = UiState.Success(Unit)
                }
                is AuthResult.Error -> {
                    _updateProfileState.value = UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }
    
    // LOAD current user
    private fun loadCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = authRepository.getCurrentUser()
        }
    }
    
    // RESET states (call when leaving screen)
    fun resetLoginState() {
        _loginState.value = UiState.Idle
    }
    fun resetRegisterState() {
        _registerState.value = UiState.Idle
    }
    fun resetResetState() {
        _resetState.value = UiState.Idle
    }
    fun resetUpdateProfileState() {
        _updateProfileState.value = UiState.Idle
    }
    
    // CHECK if user should be auto-logged in
    fun checkAutoLogin(): Flow<Boolean> {
        return sessionManager.isLoggedInFlow
    }
    
    // GET greeting based on time
    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }
}
