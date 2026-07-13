package com.kitdevelopershub.hackit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kitdevelopershub.hackit.di.AppContainer
import com.kitdevelopershub.hackit.ui.HackitApp
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * 主要フローの Robolectric E2E:
 * サインイン -> イベント情報（チームメンバー表示） -> 通知 -> 手動受付。
 * 実機/エミュレータ無しで JVM 上で回す（Mock は遅延 0 に設定）。
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MainFlowRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun signIn_viewEvent_openNotifications_manualCheckIn() {
        composeTestRule.setContent {
            HackitApp(container = AppContainer.mock(delayMillis = 0))
        }

        // サインイン画面
        composeTestRule.waitUntil(5_000) {
            composeTestRule.onAllNodes(
                androidx.compose.ui.test.hasText("お好きな方法で続けてください"),
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Google で続ける").performClick()

        // イベント情報画面: イベント名とチームメンバーが表示される
        composeTestRule.waitUntil(5_000) {
            composeTestRule.onAllNodes(
                androidx.compose.ui.test.hasText("チームメンバー"),
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Hackit 2026 夏").assertIsDisplayed()
        composeTestRule.onNodeWithText("リーダー A").assertIsDisplayed()

        // 通知画面へ
        composeTestRule.onNodeWithContentDescription("通知").performClick()
        composeTestRule.waitUntil(5_000) {
            composeTestRule.onAllNodes(
                androidx.compose.ui.test.hasText("自動受付できないとき"),
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("受付完了しました").assertIsDisplayed()

        // フェイルセーフ手動受付
        composeTestRule.onNodeWithText("手動で受付").performClick()
        composeTestRule.waitUntil(5_000) {
            composeTestRule.onAllNodes(
                androidx.compose.ui.test.hasText("受付しました"),
            ).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
