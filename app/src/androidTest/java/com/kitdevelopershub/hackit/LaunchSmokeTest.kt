package com.kitdevelopershub.hackit

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/** 実機/エミュレータ用の最小スモークテスト。 */
@RunWith(AndroidJUnit4::class)
class LaunchSmokeTest {
    @Test
    fun packageName_isReleaseId() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.kitdevelopershub.hackit", context.packageName)
    }
}
