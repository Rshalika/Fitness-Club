package com.strawhat.fitness_club

import android.app.Application
import com.strawhat.fitness_club.injection.DaggerApplicationComponent

class MyApplication : Application() {

    val appComponent = DaggerApplicationComponent.create()


}