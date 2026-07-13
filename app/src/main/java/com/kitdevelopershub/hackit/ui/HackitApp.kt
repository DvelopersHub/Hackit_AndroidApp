package com.kitdevelopershub.hackit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kitdevelopershub.hackit.di.AppContainer
import com.kitdevelopershub.hackit.model.Event
import com.kitdevelopershub.hackit.ui.common.FullScreenLoading
import com.kitdevelopershub.hackit.ui.event.EventInfoScreen
import com.kitdevelopershub.hackit.ui.event.EventInfoViewModel
import com.kitdevelopershub.hackit.ui.event.SidebarContent
import com.kitdevelopershub.hackit.ui.notifications.NotificationScreen
import com.kitdevelopershub.hackit.ui.notifications.NotificationViewModel
import com.kitdevelopershub.hackit.ui.signin.EmailEntryScreen
import com.kitdevelopershub.hackit.ui.signin.PhoneEntryScreen
import com.kitdevelopershub.hackit.ui.signin.SignInScreen
import com.kitdevelopershub.hackit.ui.signin.SignInViewModel
import com.kitdevelopershub.hackit.ui.theme.HackitColors
import com.kitdevelopershub.hackit.ui.theme.HackitTheme
import kotlinx.coroutines.launch

/**
 * ルート切り替え（iOS `RootView` 相当）:
 * 起動時セッション確認中はスピナー、未サインインならサインインフロー、
 * サインイン済みならイベント情報フロー。
 */
@Composable
fun HackitApp(container: AppContainer) {
    HackitTheme {
        val user by container.sessionStore.user.collectAsState()
        val isBootstrapping by container.sessionStore.isBootstrapping.collectAsState()

        LaunchedEffect(Unit) {
            if (isBootstrapping) {
                container.sessionStore.setUser(container.authRepository.currentUser())
                container.sessionStore.finishBootstrap()
            }
        }

        when {
            isBootstrapping -> FullScreenLoading(
                Modifier
                    .fillMaxSize()
                    .background(HackitColors.WarmOffWhite),
            )
            user == null -> SignInFlow(container)
            else -> MainFlow(container)
        }
    }
}

/** サインイン+メール/電話入力の NavHost。SignInViewModel はフロー内で共有（iOS と同じ）。 */
@Composable
private fun SignInFlow(container: AppContainer) {
    val navController = rememberNavController()
    val viewModel: SignInViewModel = viewModel {
        SignInViewModel(container.authRepository, container.sessionStore)
    }

    NavHost(navController = navController, startDestination = "signin") {
        composable("signin") {
            SignInScreen(
                viewModel = viewModel,
                onNavigateToEmail = { navController.navigate("email") },
                onNavigateToPhone = { navController.navigate("phone") },
            )
        }
        composable("email") {
            EmailEntryScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("phone") {
            PhoneEntryScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}

/** イベント情報+通知の NavHost。サイドバー Drawer をここで持つ。 */
@Composable
private fun MainFlow(container: AppContainer) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val eventViewModel: EventInfoViewModel = viewModel {
        EventInfoViewModel(container.eventRepository, container.checkInRepository)
    }

    // 通知画面へ渡すイベント（手動受付フェイルセーフの配線に使う）
    var notificationEvent by remember { mutableStateOf<Event?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = HackitColors.WarmOffWhite,
                modifier = Modifier.fillMaxWidth(0.7f),
            ) {
                SidebarContent(
                    onSignOut = {
                        scope.launch {
                            drawerState.close()
                            container.authRepository.signOut()
                            container.sessionStore.setUser(null)
                        }
                    },
                    onClose = { scope.launch { drawerState.close() } },
                )
            }
        },
    ) {
        NavHost(navController = navController, startDestination = "event") {
            composable("event") {
                EventInfoScreen(
                    viewModel = eventViewModel,
                    onOpenNotifications = { event ->
                        notificationEvent = event
                        navController.navigate("notifications")
                    },
                    onOpenSidebar = { scope.launch { drawerState.open() } },
                )
            }
            composable("notifications") {
                val event = notificationEvent
                val notificationViewModel: NotificationViewModel = viewModel {
                    NotificationViewModel(
                        notificationRepository = container.notificationRepository,
                        checkInRepository = container.checkInRepository,
                        eventId = event?.id,
                        venueLatitude = event?.venueLatitude,
                        venueLongitude = event?.venueLongitude,
                    )
                }
                NotificationScreen(
                    viewModel = notificationViewModel,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
