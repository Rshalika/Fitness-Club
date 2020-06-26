package com.strawhat.fitness_club.services

import com.strawhat.fitness_club.services.binding.MembersBinding
import com.strawhat.fitness_club.services.binding.toViewModel
import com.strawhat.fitness_club.view.ClubInfoViewModel
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
                currentUserNumber = res.myPosition
                return@map res
            }
    }

    fun getMembersInfo(id: Int): Observable<MembersBinding> {
        return apiService.getMembersInfo(id.toString())
            .subscribeOn(Schedulers.io())
    }
}