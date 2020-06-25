package com.strawhat.fitness_club.injection

import com.strawhat.fitness_club.services.ClubRepository
import com.strawhat.fitness_club.vm.MainViewModel
import dagger.Component

@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun clubRepository(): ClubRepository

    fun inject(activity: MainViewModel)

}