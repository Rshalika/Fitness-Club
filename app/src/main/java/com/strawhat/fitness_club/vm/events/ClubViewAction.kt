package com.strawhat.fitness_club.vm.events

sealed class ClubViewAction
object ClubViewLoadAction : ClubViewAction()
object ClubViewInitialLoadAction : ClubViewAction()
data class LastVisibleItemChangedAction(val position: Int) : ClubViewAction()