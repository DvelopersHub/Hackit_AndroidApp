package com.kitdevelopershub.hackit

import com.kitdevelopershub.hackit.model.MentorCallStatus
import com.kitdevelopershub.hackit.model.TechArea
import com.kitdevelopershub.hackit.ui.common.LoadPhase
import com.kitdevelopershub.hackit.ui.mentor.MentorCallViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MentorCallViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    @Test
    fun `requestCall sets active call with waiting status and queue position`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repo = FakeMentorRepository()
            val viewModel = MentorCallViewModel(repo)
            viewModel.setTechArea(TechArea.BACKEND)
            viewModel.setTableNumber("A-3")

            viewModel.requestCall()
            advanceUntilIdle()

            val call = viewModel.uiState.value.activeCall
            assertEquals(MentorCallStatus.WAITING, call?.status)
            assertEquals(2, call?.queuePosition)
            assertEquals(TechArea.BACKEND, call?.techArea)
            assertFalse(viewModel.uiState.value.isSubmitting)
        }

    @Test
    fun `duplicate submit is guarded while a call is active`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repo = FakeMentorRepository()
            val viewModel = MentorCallViewModel(repo)

            viewModel.requestCall()
            advanceUntilIdle()
            viewModel.requestCall()
            advanceUntilIdle()

            assertEquals(1, repo.requestCalls)
        }

    @Test
    fun `cancelCall clears the active call`() = runTest(mainDispatcherRule.dispatcher) {
        val repo = FakeMentorRepository()
        val viewModel = MentorCallViewModel(repo)

        viewModel.requestCall()
        advanceUntilIdle()
        viewModel.cancelCall()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.activeCall)
        assertEquals(1, repo.cancelCalls)
    }

    @Test
    fun `load surfaces an existing active call`() = runTest(mainDispatcherRule.dispatcher) {
        val repo = FakeMentorRepository()
        // 既存の呼び出しを1件仕込んでからロード。
        MentorCallViewModel(repo).apply {
            requestCall()
        }
        advanceUntilIdle()

        val viewModel = MentorCallViewModel(repo)
        viewModel.load()
        advanceUntilIdle()

        assertEquals(LoadPhase.LOADED, viewModel.uiState.value.phase)
        assertTrue(viewModel.uiState.value.hasActiveCall)
    }

    @Test
    fun `request failure sets error and leaves no active call`() =
        runTest(mainDispatcherRule.dispatcher) {
            val repo = FakeMentorRepository(requestFails = true)
            val viewModel = MentorCallViewModel(repo)

            viewModel.requestCall()
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.errorMessage)
            assertNull(viewModel.uiState.value.activeCall)
            assertFalse(viewModel.uiState.value.isSubmitting)
        }
}
