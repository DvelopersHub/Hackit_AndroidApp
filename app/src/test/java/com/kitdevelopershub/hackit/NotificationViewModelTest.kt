package com.kitdevelopershub.hackit

import com.kitdevelopershub.hackit.model.AppNotification
import com.kitdevelopershub.hackit.ui.common.LoadPhase
import com.kitdevelopershub.hackit.ui.notifications.NotificationViewModel
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private fun viewModelWithCheckIn(
        notifications: FakeNotificationRepository = FakeNotificationRepository(),
        checkIn: FakeCheckInRepository = FakeCheckInRepository(),
    ) = NotificationViewModel(
        notificationRepository = notifications,
        checkInRepository = checkIn,
        eventId = testEvent.id,
        venueLatitude = testEvent.venueLatitude,
        venueLongitude = testEvent.venueLongitude,
    )

    @Test
    fun `load success shows notifications newest first`() = runTest(mainDispatcherRule.dispatcher) {
        val items = listOf(
            AppNotification("n1", AppNotification.Kind.CHECK_IN_SUCCESS, "t1", "b1", Instant.now()),
        )
        val viewModel = NotificationViewModel(FakeNotificationRepository(notifications = { items }))

        viewModel.load()
        advanceUntilIdle()

        assertEquals(LoadPhase.LOADED, viewModel.uiState.value.phase)
        assertEquals(items, viewModel.uiState.value.notifications)
    }

    @Test
    fun `load failure enters error phase`() = runTest(mainDispatcherRule.dispatcher) {
        val repo = FakeNotificationRepository(notifications = { throw RuntimeException("network") })
        val viewModel = NotificationViewModel(repo)

        viewModel.load()
        advanceUntilIdle()

        assertEquals(LoadPhase.ERROR, viewModel.uiState.value.phase)
    }

    @Test
    fun `manual check in requires full wiring`() {
        val withoutEvent = NotificationViewModel(FakeNotificationRepository())
        assertFalse(withoutEvent.canManualCheckIn)

        val withEvent = viewModelWithCheckIn()
        assertTrue(withEvent.canManualCheckIn)
    }

    @Test
    fun `manual check in success flips didManualCheckIn`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = viewModelWithCheckIn()

        viewModel.manualCheckIn()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.didManualCheckIn)
        assertFalse(viewModel.uiState.value.manualCheckInError)
        assertFalse(viewModel.uiState.value.isManualCheckingIn)
    }

    @Test
    fun `manual check in failure keeps list intact and shows inline error`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = viewModelWithCheckIn(checkIn = FakeCheckInRepository(enterFails = true))

        viewModel.load()
        advanceUntilIdle()
        viewModel.manualCheckIn()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.manualCheckInError)
        assertFalse(viewModel.uiState.value.didManualCheckIn)
        assertEquals(LoadPhase.LOADED, viewModel.uiState.value.phase)
    }
}
