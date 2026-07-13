package com.kitdevelopershub.hackit

import com.kitdevelopershub.hackit.data.AuthRepository
import com.kitdevelopershub.hackit.data.CheckInRepository
import com.kitdevelopershub.hackit.data.EventRepository
import com.kitdevelopershub.hackit.data.NotificationRepository
import com.kitdevelopershub.hackit.model.AppNotification
import com.kitdevelopershub.hackit.model.AttendanceStatus
import com.kitdevelopershub.hackit.model.AuthProvider
import com.kitdevelopershub.hackit.model.CheckInRecord
import com.kitdevelopershub.hackit.model.Event
import com.kitdevelopershub.hackit.model.TeamMember
import com.kitdevelopershub.hackit.model.User
import java.time.Instant

// 各テストで挙動を差し替えられる素朴なフェイク群。

val testUser = User("u-1", "Test User", "test@example.com", null, isLeader = false)

val testEvent = Event(
    id = "event-test",
    name = "Hackit Test",
    startsAt = Instant.parse("2026-08-01T01:00:00Z"),
    endsAt = Instant.parse("2026-08-02T10:00:00Z"),
    venueName = "Test Venue",
    venueLatitude = 36.5,
    venueLongitude = 136.6,
    venueRadiusMeters = 80.0,
)

class FakeAuthRepository(
    var signInResult: () -> User = { testUser },
) : AuthRepository {
    var signInCalls = 0
        private set

    override suspend fun signIn(provider: AuthProvider): User {
        signInCalls++
        return signInResult()
    }

    override suspend fun signUp(provider: AuthProvider): User = signIn(provider)
    override suspend fun signOut() = Unit
    override suspend fun currentUser(): User? = null
}

class FakeEventRepository(
    var event: () -> Event = { testEvent },
    var members: () -> List<TeamMember> = {
        listOf(TeamMember("m-1", "Member 1", isLeader = true, status = AttendanceStatus.PRESENT))
    },
) : EventRepository {
    override suspend fun fetchCurrentEvent(): Event = event()
    override suspend fun fetchTeamMembers(eventId: String): List<TeamMember> = members()
}

class FakeCheckInRepository(
    var status: AttendanceStatus = AttendanceStatus.NOT_ARRIVED,
    var enterFails: Boolean = false,
) : CheckInRepository {
    override suspend fun recordEnter(eventId: String, latitude: Double, longitude: Double): CheckInRecord {
        if (enterFails) throw RuntimeException("enter failed")
        status = AttendanceStatus.PRESENT
        return CheckInRecord("r-1", eventId, "u-1", CheckInRecord.Kind.ENTER, Instant.now(), latitude, longitude)
    }

    override suspend fun recordExit(eventId: String, latitude: Double, longitude: Double): CheckInRecord {
        status = AttendanceStatus.EXITED
        return CheckInRecord("r-2", eventId, "u-1", CheckInRecord.Kind.EXIT, Instant.now(), latitude, longitude)
    }

    override suspend fun fetchOwnStatus(eventId: String): AttendanceStatus = status
}

class FakeNotificationRepository(
    var notifications: () -> List<AppNotification> = { emptyList() },
) : NotificationRepository {
    override suspend fun fetchNotifications(): List<AppNotification> = notifications()
}
