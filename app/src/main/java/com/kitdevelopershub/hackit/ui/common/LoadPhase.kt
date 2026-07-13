package com.kitdevelopershub.hackit.ui.common

/** 画面共通のロード状態（iOS `LoadPhase` と同一）。 */
enum class LoadPhase {
    IDLE,
    LOADING,
    LOADED,
    ERROR,
}
