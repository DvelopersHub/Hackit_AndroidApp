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

/** メンターの技術領域（iOS `TechArea` と同一）。表示名は UI 層で `@StringRes` にマップする。 */
enum class TechArea {
    FRONTEND,
    BACKEND,
    INFRA,
    MOBILE,
    DESIGN,
    OTHER,
}

/** メンター呼び出しの進行状態（iOS `MentorCallStatus` と同一）。 */
enum class MentorCallStatus {
    WAITING,
    IN_PROGRESS,
    DONE,
    CANCELLED,
}

/**
 * メンター呼び出し（iOS `MentorCall` と同一フィールド）。
 * `queuePosition` は待ち順（1 のとき自分の番）。順番・状態はサーバー権威。
 */
data class MentorCall(
    val id: String,
    val eventId: String,
    val teamName: String,
    val tableNumber: String,
    val techArea: TechArea,
    val message: String,
    val status: MentorCallStatus,
    val createdAt: Instant,
    val queuePosition: Int,
)

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
