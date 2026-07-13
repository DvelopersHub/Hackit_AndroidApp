package com.kitdevelopershub.hackit.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitdevelopershub.hackit.data.AuthRepository
import com.kitdevelopershub.hackit.model.AuthProvider
import com.kitdevelopershub.hackit.session.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * サインイン画面の状態（iOS `SignInViewModel` の Observable プロパティ群に対応）。
 * `loadingProvider` のボタンだけスピナーを出し、他は無効化する。
 */
data class SignInUiState(
    val loadingProvider: AuthProvider? = null,
    val errorMessageRes: Int? = null,
    val emailDraft: String = "",
    val phoneDraft: String = "",
)

class SignInViewModel(
    private val authRepository: AuthRepository,
    private val sessionStore: SessionStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    /** 多重タップは無視（iOS 実装と同じガード）。成功時は SessionStore に書き込む。 */
    fun signIn(provider: AuthProvider) {
        if (_uiState.value.loadingProvider != null) return
        _uiState.update { it.copy(loadingProvider = provider, errorMessageRes = null) }
        viewModelScope.launch {
            try {
                val user = authRepository.signIn(provider)
                sessionStore.setUser(user)
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessageRes = com.kitdevelopershub.hackit.R.string.sign_in_failed) }
            } finally {
                _uiState.update { it.copy(loadingProvider = null) }
            }
        }
    }

    fun updateEmailDraft(value: String) {
        _uiState.update { it.copy(emailDraft = value) }
    }

    /** 数字のみ受け付ける（iOS `PhoneEntryView` の onChange フィルタ相当）。 */
    fun updatePhoneDraft(value: String) {
        _uiState.update { it.copy(phoneDraft = value.filter(Char::isDigit)) }
    }

    fun submitEmail() {
        if (_uiState.value.emailDraft.isBlank()) {
            _uiState.update { it.copy(errorMessageRes = com.kitdevelopershub.hackit.R.string.email_entry_empty) }
            return
        }
        signIn(AuthProvider.EMAIL)
    }

    fun submitPhone() {
        if (_uiState.value.phoneDraft.isEmpty()) {
            _uiState.update { it.copy(errorMessageRes = com.kitdevelopershub.hackit.R.string.phone_entry_empty) }
            return
        }
        signIn(AuthProvider.PHONE)
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessageRes = null) }
    }
}
