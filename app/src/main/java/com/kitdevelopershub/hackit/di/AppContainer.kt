package com.kitdevelopershub.hackit.di

import com.kitdevelopershub.hackit.data.AuthRepository
import com.kitdevelopershub.hackit.data.CheckInRepository
import com.kitdevelopershub.hackit.data.EventRepository
import com.kitdevelopershub.hackit.data.MentorRepository
import com.kitdevelopershub.hackit.data.NotificationRepository
import com.kitdevelopershub.hackit.data.mock.MockAuthRepository
import com.kitdevelopershub.hackit.data.mock.MockCheckInRepository
import com.kitdevelopershub.hackit.data.mock.MockEventRepository
import com.kitdevelopershub.hackit.data.mock.MockMentorRepository
import com.kitdevelopershub.hackit.data.mock.MockNotificationRepository
import com.kitdevelopershub.hackit.session.SessionStore

/**
 * 手組みの軽量 DI コンテナ（iOS `AppDependencies` 相当）。
 * 実 API 移行時はここで Mock を実装クラスに差し替えるだけで済む。
 */
class AppContainer(
    val authRepository: AuthRepository,
    val eventRepository: EventRepository,
    val checkInRepository: CheckInRepository,
    val notificationRepository: NotificationRepository,
    val mentorRepository: MentorRepository,
    val sessionStore: SessionStore = SessionStore(),
) {
    companion object {
        /** MVP 既定のモック構成。`delayMillis = 0` でテスト用に即時化できる。 */
        fun mock(delayMillis: Long = 300): AppContainer = AppContainer(
            authRepository = MockAuthRepository(delayMillis),
            eventRepository = MockEventRepository(delayMillis),
            checkInRepository = MockCheckInRepository(delayMillis),
            notificationRepository = MockNotificationRepository(delayMillis),
            mentorRepository = MockMentorRepository(delayMillis),
        )
    }
}
