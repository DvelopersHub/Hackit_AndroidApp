package com.kitdevelopershub.hackit

import com.kitdevelopershub.hackit.model.AttendanceStatus
import com.kitdevelopershub.hackit.model.TeamMember
import com.kitdevelopershub.hackit.ui.common.LoadPhase
import com.kitdevelopershub.hackit.ui.event.EventInfoViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EventInfoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    @Test
    fun `load populates event members and own status`() = runTest(mainDispatcherRule.dispatcher) {
        val checkIn = FakeCheckInRepository(status = AttendanceStatus.PRESENT)
        val viewModel = EventInfoViewModel(FakeEventRepository(), checkIn)

        viewModel.load()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(LoadPhase.LOADED, state.phase)
        assertEquals(testEvent, state.event)
        assertEquals(1, state.teamMembers.size)
        assertEquals(AttendanceStatus.PRESENT, state.ownStatus)
    }

    @Test
    fun `load failure enters error phase`() = runTest(mainDispatcherRule.dispatcher) {
        val events = FakeEventRepository(event = { throw RuntimeException("network") })
        val viewModel = EventInfoViewModel(events, FakeCheckInRepository())

        viewModel.load()
        advanceUntilIdle()

        assertEquals(LoadPhase.ERROR, viewModel.uiState.value.phase)
        assertTrue(viewModel.uiState.value.loadError)
    }

    @Test
    fun `retry after failure recovers`() = runTest(mainDispatcherRule.dispatcher) {
        var fail = true
        val events = FakeEventRepository(event = {
            if (fail) throw RuntimeException("network") else testEvent
        })
        val viewModel = EventInfoViewModel(events, FakeCheckInRepository())

        viewModel.load()
        advanceUntilIdle()
        assertEquals(LoadPhase.ERROR, viewModel.uiState.value.phase)

        fail = false
        viewModel.retry()
        advanceUntilIdle()
        assertEquals(LoadPhase.LOADED, viewModel.uiState.value.phase)
    }

    @Test
    fun `polling refreshes team members every interval`() = runTest(mainDispatcherRule.dispatcher) {
        var members = listOf(
            TeamMember("m-1", "Member 1", isLeader = true, status = AttendanceStatus.NOT_ARRIVED),
        )
        val events = FakeEventRepository(members = { members })
        val viewModel = EventInfoViewModel(events, FakeCheckInRepository(), pollingIntervalMillis = 5_000)

        viewModel.load()
        advanceUntilIdle()
        assertEquals(AttendanceStatus.NOT_ARRIVED, viewModel.uiState.value.teamMembers.first().status)

        members = listOf(
            TeamMember("m-1", "Member 1", isLeader = true, status = AttendanceStatus.PRESENT),
        )
        viewModel.startPolling()
        advanceTimeBy(5_100)

        assertEquals(AttendanceStatus.PRESENT, viewModel.uiState.value.teamMembers.first().status)
        viewModel.stopPolling()
    }

    @Test
    fun `polling failure keeps last known state`() = runTest(mainDispatcherRule.dispatcher) {
        var fail = false
        val events = FakeEventRepository(members = {
            if (fail) throw RuntimeException("poll failed")
            else listOf(TeamMember("m-1", "Member 1", isLeader = false, status = AttendanceStatus.PRESENT))
        })
        val viewModel = EventInfoViewModel(events, FakeCheckInRepository(), pollingIntervalMillis = 5_000)

        viewModel.load()
        advanceUntilIdle()

        fail = true
        viewModel.startPolling()
        advanceTimeBy(5_100)

        assertEquals(LoadPhase.LOADED, viewModel.uiState.value.phase)
        assertEquals(1, viewModel.uiState.value.teamMembers.size)
        viewModel.stopPolling()
    }

    @Test
    fun `stopPolling halts refresh`() = runTest(mainDispatcherRule.dispatcher) {
        var calls = 0
        val events = FakeEventRepository(members = {
            calls++
            emptyList()
        })
        val viewModel = EventInfoViewModel(events, FakeCheckInRepository(), pollingIntervalMillis = 5_000)

        viewModel.load()
        advanceUntilIdle()
        val callsAfterLoad = calls

        viewModel.startPolling()
        viewModel.stopPolling()
        advanceTimeBy(20_000)

        assertEquals(callsAfterLoad, calls)
    }
}
