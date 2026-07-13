package com.kitdevelopershub.hackit.session

import com.kitdevelopershub.hackit.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * サインイン中ユーザーの単一情報源（iOS `SessionStore` 相当）。
 * `AppRoot` が `user` を監視してサインイン画面とイベント画面を切り替える。
 */
class SessionStore(initialUser: User? = null) {

    private val _user = MutableStateFlow(initialUser)
    val user: StateFlow<User?> = _user.asStateFlow()

    /** 起動時のキャッシュセッション確認中は true。 */
    private val _isBootstrapping = MutableStateFlow(true)
    val isBootstrapping: StateFlow<Boolean> = _isBootstrapping.asStateFlow()

    fun setUser(user: User?) {
        _user.value = user
    }

    fun finishBootstrap() {
        _isBootstrapping.value = false
    }
}
