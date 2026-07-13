package com.kitdevelopershub.hackit.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitdevelopershub.hackit.data.CheckInRepository
import com.kitdevelopershub.hackit.data.NotificationRepository
import com.kitdevelopershub.hackit.model.AppNotification
import com.kitdevelopershub.hackit.ui.common.LoadPhase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationUiState(
    val notifications: List<AppNotification> = emptyList(),
    val phase: LoadPhase = LoadPhase.IDLE,
    val loadError: Boolean = false,
    val isManualCheckingIn: Boolean = false,
    val didManualCheckIn: Boolean = false,
    val manualCheckInError: Boolean = false,
)

/**
 * 通知画面（iOS `NotificationViewModel` 相当）。
 * ジオフェンス自動受付が発火しないときのフェイルセーフ手動受付も担う。
 * 手動受付はイベント ID と会場座標が両方渡ったときだけ有効。
 */
class NotificationViewModel(
    private val notificationRepository: NotificationRepository,
    private val checkInRepository: CheckInRepository? = null,
    private val eventId: String? = null,
    private val venueLatitude: Double? = null,
    private val venueLongitude: Double? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    val canManualCheckIn: Boolean
        get() = checkInRepository != null && eventId != null &&
            venueLatitude != null && venueLongitude != null

    fun load() {
        _uiState.update { it.copy(phase = LoadPhase.LOADING, loadError = false) }
        viewModelScope.launch {
            try {
                val items = notificationRepository.fetchNotifications()
                _uiState.update { it.copy(notifications = items, phase = LoadPhase.LOADED) }
            } catch (_: Exception) {
                _uiState.update { it.copy(phase = LoadPhase.ERROR, loadError = true) }
            }
        }
    }

    fun retry() = load()

    /** リストのエラーとは別枠で手動受付エラーを持つ（画面全体を壊さないため）。 */
    fun manualCheckIn() {
        val repository = checkInRepository ?: return
        val id = eventId ?: return
        val lat = venueLatitude ?: return
        val lon = venueLongitude ?: return
        if (_uiState.value.isManualCheckingIn) return

        _uiState.update { it.copy(isManualCheckingIn = true, manualCheckInError = false) }
        viewModelScope.launch {
            try {
                repository.recordEnter(id, lat, lon)
                _uiState.update { it.copy(didManualCheckIn = true) }
            } catch (_: Exception) {
                _uiState.update { it.copy(manualCheckInError = true) }
            } finally {
                _uiState.update { it.copy(isManualCheckingIn = false) }
            }
        }
    }
}
