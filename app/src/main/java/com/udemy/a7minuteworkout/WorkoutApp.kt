package com.udemy.a7minuteworkout

import android.app.Application

internal class WorkoutApp:Application() {

    val db by lazy {
        HistoryDatabase.getInstance(this)
    }
}