package com.kitdevelopershub.hackit.ui.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitdevelopershub.hackit.data.CheckInRepository
import com.kitdevelopershub.hackit.data.EventRepository
import com.kitdevelopershub.hackit.model.AttendanceStatus
import com.kitdevelopershub.hackit.model.Event
import com.kitdevelopershub.hackit.model.TeamMember
import com.kitdevelopershub.hackit.ui.common.LoadPhase
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventInfoUiState(
    val event: Event? = null,
    val teamMembers: List<TeamMember> = emptyList(),
    val ownStatus: AttendanceStatus = AttendanceStatus.NOT_ARRIVED,
    val phase: LoadPhase = LoadPhase.IDLE,
    val loadError: Boolean = false,
)

/**
 * イベント情報画面（iOS `EventInfoViewModel` 相当）。
 * 初回ロード後、表示中は 5 秒間隔でメンバー状態と自分の状態をポーリングする（MVP 方針）。
 */
class EventInfoViewModel(
    private val eventRepository: EventRepository,
    private val checkInRepository: CheckInRepository,
    private val pollingIntervalMillis: Long = 5_000,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventInfoUiState())
    val uiState: StateFlow<EventInfoUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    /** 初回ロード。イベント取得後、メンバーと自状態を並列取得。失敗はこの経路のみ ERROR にする。 */
    fun load() {
        _uiState.update { it.copy(phase = LoadPhase.LOADING, loadError = false) }
        viewModelScope.launch {
            try {
                val event = eventRepository.fetchCurrentEvent()
                val (members, status) = coroutineScope {
                    val membersDeferred = async { eventRepository.fetchTeamMembers(event.id) }
                    val statusDeferred = async { checkInRepository.fetchOwnStatus(event.id) }
                    membersDeferred.await() to statusDeferred.await()
                }
                _uiState.update {
                    it.copy(
                        event = event,
                        teamMembers = members,
                        ownStatus = status,
                        phase = LoadPhase.LOADED,
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(phase = LoadPhase.ERROR, loadError = true) }
            }
        }
    }

    /** ポーリング開始。失敗は握りつぶして直前の表示を維持する（iOS と同方針）。 */
    fun startPolling() {
        if (pollingJob != null) return
        val eventId = _uiState.value.event?.id ?: return
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(pollingIntervalMillis)
                try {
                    val (members, status) = coroutineScope {
                        val membersDeferred = async { eventRepository.fetchTeamMembers(eventId) }
                        val statusDeferred = async { checkInRepository.fetchOwnStatus(eventId) }
                        membersDeferred.await() to statusDeferred.await()
                    }
                    _uiState.update { it.copy(teamMembers = members, ownStatus = status) }
                } catch (_: Exception) {
                    // ポーリング失敗時は最後の状態を保持
                }
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun retry() = load()

    override fun onCleared() {
        stopPolling()
    }
}
