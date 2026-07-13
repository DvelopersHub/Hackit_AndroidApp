package com.kitdevelopershub.hackit.ui.mentor

import com.kitdevelopershub.hackit.R
import com.kitdevelopershub.hackit.model.TechArea

// iOS TechArea+Display.swift 相当。表示名はモデルから UI 層へ分離する。

val TechArea.displayNameRes: Int
    get() = when (this) {
        TechArea.FRONTEND -> R.string.tech_area_frontend
        TechArea.BACKEND -> R.string.tech_area_backend
        TechArea.INFRA -> R.string.tech_area_infra
        TechArea.MOBILE -> R.string.tech_area_mobile
        TechArea.DESIGN -> R.string.tech_area_design
        TechArea.OTHER -> R.string.tech_area_other
    }
