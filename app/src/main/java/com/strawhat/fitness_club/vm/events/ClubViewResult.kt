package com.strawhat.fitness_club.vm.events

import com.strawhat.fitness_club.view.ClubInfoViewModel
import com.strawhat.fitness_club.view.ClubMemberViewModel

sealed class ClubViewResult
data class ClubViewInitialLoadSuccessResult(
    val clubInfo: ClubInfoViewModel,
    val members: List<ClubMemberViewModel>,
    val hasMore: Boolean
) : ClubViewResult()

data class ClubViewLoadSuccessResult(
    val members: List<ClubMemberViewModel>,
    val hasMore: Boolean
) : ClubViewResult()

object ClubViewLoadEndedResult : ClubViewResult()

data class ClubViewLoadErrorResult(val throwable: Throwable) : ClubViewResult()

object ClubLoadingResult : ClubViewResult()
object ClubLoadPageRequestedResult : ClubViewResult()
data class LastVisibleItemChangedResult(val position: Int) : ClubViewResult()