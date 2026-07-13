package com.kitdevelopershub.hackit.model

import java.time.Instant

/** iOS `User` と同一フィールド。 */
data class User(
    val id: String,
    val displayName: String,
    val email: String?,
    val githubUrl: String?,
    val isLeader: Boolean,
)

/** iOS `Event` と同一フィールド。会場座標・半径はサーバー権威（ADR-0008）。 */
data class Event(
    val id: String,
    val name: String,
    val startsAt: Instant,
    val endsAt: Instant,
    val venueName: String,
    val venueLatitude: Double,
    val venueLongitude: Double,
    val venueRadiusMeters: Double,
)

enum class AttendanceStatus {
    NOT_ARRIVED,
    PRESENT,
    EXITED,
}

data class TeamMember(
    val id: String,
    val displayName: String,
    val isLeader: Boolean,
    val status: AttendanceStatus,
)

data class CheckInRecord(
    val id: String,
    val eventId: String,
    val userId: String,
    val kind: Kind,
    val timestamp: Instant,
    val latitude: Double,
    val longitude: Double,
) {
    enum class Kind { ENTER, EXIT }
}

/** アプリ内通知。`kind` がアイコンと色を決める（iOS `AppNotification` と同一）。 */
data class AppNotification(
    val id: String,
    val kind: Kind,
    val title: String,
    val body: String,
    val timestamp: Instant,
) {
    enum class Kind {
        /** ジオフェンス内で自動チェックイン成功。 */
        CHECK_IN_SUCCESS,

        /** 会場ジオフェンスから退出。 */
        CHECK_OUT,

        /** 将来用（開始前リマインド）。MVP では未使用。 */
        REMINDER,
    }
}
