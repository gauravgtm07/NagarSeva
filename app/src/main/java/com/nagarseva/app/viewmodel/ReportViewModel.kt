package com.nagarseva.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nagarseva.app.data.local.entity.ReportEntity
import com.nagarseva.app.data.repository.AuthResult
import com.nagarseva.app.data.repository.ReportRepository
import com.nagarseva.app.util.SessionManager
import com.nagarseva.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    // ── Submit Report State ──────────────
    private val _submitState = 
        MutableStateFlow<UiState<ReportEntity>>(
            UiState.Idle)
    val submitState: 
        StateFlow<UiState<ReportEntity>> = 
        _submitState.asStateFlow()
    
    // ── My Reports List ──────────────────
    private val _selectedFilter = 
        MutableStateFlow<String?>(null) // null = ALL
    val selectedFilter = _selectedFilter.asStateFlow()
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val userReports: StateFlow<List<ReportEntity>> = 
        _selectedFilter.flatMapLatest { filter ->
            reportRepository.getFilteredReportsFlow(filter)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // ── Stats for Home Screen ────────────
    val userStats: StateFlow<Triple<Int, Int, Int>> = 
        reportRepository.getUserStatsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Triple(0, 0, 0)
        )
    // Triple: (total, resolved, pending)
    
    // ── Recent Activity ──────────────────
    val recentActivity: 
        StateFlow<List<ReportEntity>> = 
        reportRepository.getRecentActivityFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // ── Tracker State ────────────────────
    private val _trackerState = 
        MutableStateFlow<UiState<ReportEntity>>(
            UiState.Idle)
    val trackerState: 
        StateFlow<UiState<ReportEntity>> = 
        _trackerState.asStateFlow()
    
    // ── Report Detail State ──────────────
    private val _reportDetailState = 
        MutableStateFlow<UiState<ReportEntity>>(
            UiState.Idle)
    val reportDetailState: 
        StateFlow<UiState<ReportEntity>> = 
        _reportDetailState.asStateFlow()
    
    // ── Delete State ─────────────────────
    private val _deleteState = 
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = 
        _deleteState.asStateFlow()
    
    // SUBMIT REPORT
    fun submitReport(
        issueTitle: String,
        defectType: String,
        severity: String,
        latitude: Double,
        longitude: Double,
        locationAccuracy: Float,
        address: String,
        photoPath: String,
        description: String
    ) {
        viewModelScope.launch {
            _submitState.value = UiState.Loading
            
            when (val result = 
                reportRepository.submitReport(
                    issueTitle = issueTitle,
                    defectType = defectType,
                    severity = severity,
                    latitude = latitude,
                    longitude = longitude,
                    locationAccuracy = locationAccuracy,
                    address = address,
                    photoPath = photoPath,
                    description = description
                )) {
                is AuthResult.Success -> {
                    _submitState.value = 
                        UiState.Success(result.data)
                }
                is AuthResult.Error -> {
                    _submitState.value = 
                        UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }
    
    // TRACK by ticket ID
    fun trackReport(ticketId: String) {
        viewModelScope.launch {
            _trackerState.value = UiState.Loading
            delay(300) // brief loading for UX
            
            when (val result = 
                reportRepository.trackReport(ticketId)) {
                is AuthResult.Success -> {
                    _trackerState.value = 
                        UiState.Success(result.data)
                }
                is AuthResult.Error -> {
                    _trackerState.value = 
                        UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }
    
    // LOAD report detail
    fun loadReportDetail(reportId: Long) {
        viewModelScope.launch {
            _reportDetailState.value = UiState.Loading
            
            when (val result = 
                reportRepository.getReportById(
                    reportId)) {
                is AuthResult.Success -> {
                    _reportDetailState.value = 
                        UiState.Success(result.data)
                }
                is AuthResult.Error -> {
                    _reportDetailState.value = 
                        UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }
    
    // SET filter for My Reports
    fun setFilter(status: String?) {
        _selectedFilter.value = status
    }
    
    // DELETE report
    fun deleteReport(reportId: Long) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            when (val result = 
                reportRepository.deleteReport(reportId)){
                is AuthResult.Success -> {
                    _deleteState.value = 
                        UiState.Success(Unit)
                }
                is AuthResult.Error -> {
                    _deleteState.value = 
                        UiState.Error(result.message)
                }
                else -> {}
            }
        }
    }
    
    // RESET states
    fun resetSubmitState() {
        _submitState.value = UiState.Idle
    }
    fun resetTrackerState() {
        _trackerState.value = UiState.Idle
    }
    fun resetDeleteState() {
        _deleteState.value = UiState.Idle
    }
}
