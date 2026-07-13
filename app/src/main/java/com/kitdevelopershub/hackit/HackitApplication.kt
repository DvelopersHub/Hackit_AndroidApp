package com.kitdevelopershub.hackit

import android.app.Application
import com.kitdevelopershub.hackit.di.AppContainer

class HackitApplication : Application() {
    /** アプリ全体で共有する依存コンテナ。MVP は Mock 構成（ADR-0004 Mock-first）。 */
    val container: AppContainer by lazy { AppContainer.mock() }
}
