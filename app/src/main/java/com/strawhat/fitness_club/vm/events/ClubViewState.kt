package com.strawhat.fitness_club.vm.events

import com.strawhat.fitness_club.view.ClubInfoViewModel
import com.strawhat.fitness_club.view.ClubMemberViewModel

data class ClubViewState(
    val info: ClubInfoViewModel? = null,
    val members: MutableList<ClubMemberViewModel> = mutableListOf(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val hasMore: Boolean = true,
    val lastPage: Int = 0,
    val lastVisiblePosition: Int = 0,
    val changedMembers: Boolean = false
)