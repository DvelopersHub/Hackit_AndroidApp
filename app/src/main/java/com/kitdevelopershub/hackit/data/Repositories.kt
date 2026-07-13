package com.kitdevelopershub.hackit.data

import com.kitdevelopershub.hackit.model.AppNotification
import com.kitdevelopershub.hackit.model.AttendanceStatus
import com.kitdevelopershub.hackit.model.AuthProvider
import com.kitdevelopershub.hackit.model.CheckInRecord
import com.kitdevelopershub.hackit.model.Event
import com.kitdevelopershub.hackit.model.MentorCall
import com.kitdevelopershub.hackit.model.TeamMember
import com.kitdevelopershub.hackit.model.TechArea
import com.kitdevelopershub.hackit.model.User

// iOS の Repository プロトコル群と 1:1 対応。
// 実 API（BuildConfig.API_BASE_URL）確定後は実装クラスの差し替えのみで移行する（ADR-0004 Mock-first）。

interface AuthRepository {
    suspend fun signIn(provider: AuthProvider): User
    suspend fun signUp(provider: AuthProvider): User
    suspend fun signOut()
    suspend fun currentUser(): User?
}

interface EventRepository {
    suspend fun fetchCurrentEvent(): Event
    suspend fun fetchTeamMembers(eventId: String): List<TeamMember>
}

interface CheckInRepository {
    suspend fun recordEnter(eventId: String, latitude: Double, longitude: Double): CheckInRecord
    suspend fun recordExit(eventId: String, latitude: Double, longitude: Double): CheckInRecord
    suspend fun fetchOwnStatus(eventId: String): AttendanceStatus
}

interface NotificationRepository {
    /** 新しい順で返す想定（Mock も準拠）。 */
    suspend fun fetchNotifications(): List<AppNotification>
}

interface MentorRepository {
    /** メンター呼び出しを起票。WAITING の `MentorCall`（待ち順つき）を返す。 */
    suspend fun requestCall(
        eventId: String,
        teamName: String,
        tableNumber: String,
        techArea: TechArea,
        message: String,
    ): MentorCall

    /** 呼び出しを取り消す。 */
    suspend fun cancelCall(callId: String)

    /** 進行中の呼び出しを返す。無ければ null。 */
    suspend fun fetchActiveCall(eventId: String): MentorCall?
}
