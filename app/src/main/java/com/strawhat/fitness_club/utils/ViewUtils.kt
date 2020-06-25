package com.strawhat.fitness_club.utils

import android.view.View

fun Boolean?.toVisibility(): Int {
    return if (this == true) {
        View.VISIBLE
    } else {
        View.GONE
    }
}