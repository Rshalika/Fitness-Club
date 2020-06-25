package com.strawhat.fitness_club.services

import com.strawhat.fitness_club.services.binding.toViewModel
import com.strawhat.fitness_club.view.ClubInfoViewModel
import com.strawhat.fitness_club.view.ClubMemberViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers


class ClubRepository(private val apiService: ApiService) {

    private var currentUserNumber: Int? = null

    private var cachedUsers: Int? = null

    fun getClubInfo(): Observable<ClubInfoViewModel> {
        return apiService.getClubInfo()
            .subscribeOn(Schedulers.io())
            .map {
                val res = it.toViewModel()
                currentUserNumber = res.position
                return@map res
            }
    }

    fun getMembersInfo(id: Int): Observable<List<ClubMemberViewModel>> {
        return apiService.getMembersInfo(id.toString())
            .subscribeOn(Schedulers.io())
            .map {
                val res = it.toViewModel()
                res.forEach { membersModel ->
                    if (membersModel.number == currentUserNumber) {
                        membersModel.isCurrentUser = true
                    }
                }
                return@map res
            }
    }
}