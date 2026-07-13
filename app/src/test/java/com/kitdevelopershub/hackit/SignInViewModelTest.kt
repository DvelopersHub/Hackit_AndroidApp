package com.kitdevelopershub.hackit

import com.kitdevelopershub.hackit.model.AuthProvider
import com.kitdevelopershub.hackit.session.SessionStore
import com.kitdevelopershub.hackit.ui.signin.SignInViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val sessionStore = SessionStore()

    @Test
    fun `signIn success writes user to session store`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = SignInViewModel(FakeAuthRepository(), sessionStore)

        viewModel.signIn(AuthProvider.GOOGLE)
        advanceUntilIdle()

        assertEquals(testUser, sessionStore.user.value)
        assertNull(viewModel.uiState.value.loadingProvider)
        assertNull(viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `signIn failure surfaces error and keeps user signed out`() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeAuthRepository(signInResult = { throw RuntimeException("boom") })
        val viewModel = SignInViewModel(repository, sessionStore)

        viewModel.signIn(AuthProvider.GITHUB)
        advanceUntilIdle()

        assertNull(sessionStore.user.value)
        assertEquals(R.string.sign_in_failed, viewModel.uiState.value.errorMessageRes)
        assertNull(viewModel.uiState.value.loadingProvider)
    }

    @Test
    fun `second signIn while in flight is dropped`() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = SignInViewModel(repository, sessionStore)

        viewModel.signIn(AuthProvider.GOOGLE)
        viewModel.signIn(AuthProvider.APPLE)
        advanceUntilIdle()

        assertEquals(1, repository.signInCalls)
    }

    @Test
    fun `submitEmail with blank draft shows validation error`() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = SignInViewModel(repository, sessionStore)

        viewModel.submitEmail()
        advanceUntilIdle()

        assertEquals(R.string.email_entry_empty, viewModel.uiState.value.errorMessageRes)
        assertEquals(0, repository.signInCalls)
    }

    @Test
    fun `submitEmail with draft signs in`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = SignInViewModel(FakeAuthRepository(), sessionStore)

        viewModel.updateEmailDraft("user@example.com")
        viewModel.submitEmail()
        advanceUntilIdle()

        assertNotNull(sessionStore.user.value)
    }

    @Test
    fun `phone draft keeps digits only`() {
        val viewModel = SignInViewModel(FakeAuthRepository(), sessionStore)

        viewModel.updatePhoneDraft("090-1234-abc5678")

        assertEquals("09012345678", viewModel.uiState.value.phoneDraft)
    }

    @Test
    fun `submitPhone with empty draft shows validation error`() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = SignInViewModel(repository, sessionStore)

        viewModel.submitPhone()
        advanceUntilIdle()

        assertEquals(R.string.phone_entry_empty, viewModel.uiState.value.errorMessageRes)
        assertEquals(0, repository.signInCalls)
    }

    @Test
    fun `dismissError clears error`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = SignInViewModel(FakeAuthRepository(), sessionStore)

        viewModel.submitEmail()
        viewModel.dismissError()

        assertNull(viewModel.uiState.value.errorMessageRes)
    }
}
