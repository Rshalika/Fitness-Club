package com.strawhat.fitness_club.services

import com.strawhat.fitness_club.services.binding.ClubInfoBinding
import com.strawhat.fitness_club.services.binding.MembersBinding
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("info.json")
    fun getClubInfo(): Observable<ClubInfoBinding>

    @GET("members/{id}.json")
    fun getMembersInfo(@Path("id") id: String): Observable<MembersBinding>

}