package com.strawhat.fitness_club.vm.events

import com.strawhat.fitness_club.view.ClubInfoViewModel
import com.strawhat.fitness_club.view.ClubMemberViewModel

data class ClubViewState(
    val info: ClubInfoViewModel? = null,
    val members: List<ClubMemberViewModel> = listOf(),
    val loading: Boolean = false,
    val errorMessage: String? = null
)