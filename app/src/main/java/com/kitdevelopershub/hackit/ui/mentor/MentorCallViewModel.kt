package com.kitdevelopershub.hackit.ui.mentor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitdevelopershub.hackit.data.MentorRepository
import com.kitdevelopershub.hackit.model.MentorCall
import com.kitdevelopershub.hackit.model.TechArea
import com.kitdevelopershub.hackit.ui.common.LoadPhase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MentorCallUiState(
    val activeCall: MentorCall? = null,
    val selectedTechArea: TechArea = TechArea.FRONTEND,
    val tableNumber: String = "",
    val message: String = "",
    val isSubmitting: Boolean = false,
    val phase: LoadPhase = LoadPhase.IDLE,
    val errorMessage: Boolean = false,
) {
    /** 進行中の呼び出しがある間はフォームを閉じる（二重起票防止の表示条件）。 */
    val hasActiveCall: Boolean get() = activeCall != null
}

/**
 * メンター呼び出し画面（iOS `MentorCallViewModel` 相当）。
 * 進行中の呼び出しが 1 件あれば入力フォームを隠し、状態カードと取消だけを見せる。
 */
class MentorCallViewModel(
    private val mentorRepository: MentorRepository,
    private val eventId: String = "event-001",
) : ViewModel() {

    private val _uiState = MutableStateFlow(MentorCallUiState())
    val uiState: StateFlow<MentorCallUiState> = _uiState.asStateFlow()

    /** 初回ロード。進行中の呼び出しがあれば取得して状態カードを出す。 */
    fun load() {
        _uiState.update { it.copy(phase = LoadPhase.LOADING, errorMessage = false) }
        viewModelScope.launch {
            try {
                val active = mentorRepository.fetchActiveCall(eventId)
                _uiState.update { it.copy(activeCall = active, phase = LoadPhase.LOADED) }
            } catch (_: Exception) {
                _uiState.update { it.copy(phase = LoadPhase.ERROR, errorMessage = true) }
            }
        }
    }

    fun setTechArea(area: TechArea) = _uiState.update { it.copy(selectedTechArea = area) }

    fun setTableNumber(value: String) = _uiState.update { it.copy(tableNumber = value) }

    fun setMessage(value: String) = _uiState.update { it.copy(message = value) }

    /** メンターを呼ぶ。進行中の呼び出しがある / 送信中は二重起票を防ぐ。 */
    fun requestCall() {
        val state = _uiState.value
        if (state.hasActiveCall || state.isSubmitting) return

        _uiState.update { it.copy(isSubmitting = true, errorMessage = false) }
        viewModelScope.launch {
            try {
                val call = mentorRepository.requestCall(
                    eventId = eventId,
                    teamName = TEAM_NAME,
                    tableNumber = state.tableNumber,
                    techArea = state.selectedTechArea,
                    message = state.message,
                )
                _uiState.update { it.copy(activeCall = call) }
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessage = true) }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    /** 呼び出しを取り消してフォームへ戻す。 */
    fun cancelCall() {
        val call = _uiState.value.activeCall ?: return
        if (_uiState.value.isSubmitting) return

        _uiState.update { it.copy(isSubmitting = true, errorMessage = false) }
        viewModelScope.launch {
            try {
                mentorRepository.cancelCall(call.id)
                _uiState.update { it.copy(activeCall = null) }
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessage = true) }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    private companion object {
        // TODO(backend): チーム名はサーバーのセッションから解決する。MVP は固定値。
        const val TEAM_NAME = "マイチーム"
    }
}
