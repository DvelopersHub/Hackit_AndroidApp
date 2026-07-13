package com.kitdevelopershub.hackit.data.mock

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
import java.time.Duration
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.delay

// iOS の Mock 群と同じ内容・同じ人工遅延。`delayMillis` はテストで 0 にできる。

class MockAuthRepository(private val delayMillis: Long = 300) : AuthRepository {

    private var loggedIn: User? = null

    override suspend fun signIn(provider: AuthProvider): User {
        delay(delayMillis)
        return makeUser(provider).also { loggedIn = it }
    }

    override suspend fun signUp(provider: AuthProvider): User = signIn(provider)

    override suspend fun signOut() {
        loggedIn = null
    }

    override suspend fun currentUser(): User? = loggedIn

    private fun makeUser(provider: AuthProvider): User {
        val key = provider.name.lowercase()
        return User(
            id = "mock-user-$key",
            displayName = "${key.replaceFirstChar { it.uppercase() }} User",
            email = if (provider == AuthProvider.PHONE) null else "$key@example.com",
            githubUrl = if (provider == AuthProvider.GITHUB) "https://github.com/mock" else null,
            isLeader = true,
        )
    }
}

class MockEventRepository(private val delayMillis: Long = 200) : EventRepository {

    override suspend fun fetchCurrentEvent(): Event {
        delay(delayMillis)
        val now = Instant.now()
        return Event(
            id = "event-001",
            name = "Hackit 2026 夏",
            startsAt = now.plus(Duration.ofHours(1)),
            endsAt = now.plus(Duration.ofHours(49)),
            venueName = "金沢工業大学",
            venueLatitude = 36.5325,
            venueLongitude = 136.6280,
            venueRadiusMeters = 80.0,
        )
    }

    override suspend fun fetchTeamMembers(eventId: String): List<TeamMember> {
        delay(delayMillis)
        return listOf(
            TeamMember("m-1", "リーダー A", isLeader = true, status = AttendanceStatus.PRESENT),
            TeamMember("m-2", "メンバー B", isLeader = false, status = AttendanceStatus.NOT_ARRIVED),
            TeamMember("m-3", "メンバー C", isLeader = false, status = AttendanceStatus.EXITED),
        )
    }
}

class MockCheckInRepository(private val delayMillis: Long = 200) : CheckInRepository {

    private var status: AttendanceStatus = AttendanceStatus.NOT_ARRIVED

    override suspend fun recordEnter(eventId: String, latitude: Double, longitude: Double): CheckInRecord {
        delay(delayMillis)
        status = AttendanceStatus.PRESENT
        return record(eventId, CheckInRecord.Kind.ENTER, latitude, longitude)
    }

    override suspend fun recordExit(eventId: String, latitude: Double, longitude: Double): CheckInRecord {
        delay(delayMillis)
        status = AttendanceStatus.EXITED
        return record(eventId, CheckInRecord.Kind.EXIT, latitude, longitude)
    }

    override suspend fun fetchOwnStatus(eventId: String): AttendanceStatus = status

    private fun record(
        eventId: String,
        kind: CheckInRecord.Kind,
        latitude: Double,
        longitude: Double,
    ) = CheckInRecord(
        id = UUID.randomUUID().toString(),
        eventId = eventId,
        userId = "mock-user-1",
        kind = kind,
        timestamp = Instant.now(),
        latitude = latitude,
        longitude = longitude,
    )
}

class MockNotificationRepository(private val delayMillis: Long = 300) : NotificationRepository {

    override suspend fun fetchNotifications(): List<AppNotification> {
        delay(delayMillis)
        val now = Instant.now()
        return listOf(
            AppNotification(
                id = "n1",
                kind = AppNotification.Kind.CHECK_IN_SUCCESS,
                title = "受付完了しました",
                body = "Hackit 2026 夏の会場に到着しました。素敵なハッカソンを！",
                timestamp = now.minus(Duration.ofMinutes(30)),
            ),
            AppNotification(
                id = "n2",
                kind = AppNotification.Kind.CHECK_OUT,
                title = "会場から退出しました",
                body = "Hackit 2026 夏の会場から退出しました。お疲れさまでした。",
                timestamp = now.minus(Duration.ofHours(1)),
            ),
        )
    }
}
