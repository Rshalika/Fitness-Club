package com.strawhat.fitness_club.vm.events

import com.strawhat.fitness_club.view.ClubInfoViewModel
import com.strawhat.fitness_club.view.ClubMemberViewModel

sealed class ClubViewResult
data class ClubViewLoadSuccessResult(
    val clubInfo: ClubInfoViewModel,
    val members: List<ClubMemberViewModel>
) : ClubViewResult()

data class ClubViewLoadErrorResult(val throwable: Throwable) : ClubViewResult()

object ClubLoadingResult : ClubViewResult()