package com.strawhat.fitness_club.view

data class ClubMemberViewModel(

    val number: Int,

    val name: String,

    val image: String,

    var time: String,

    var isCurrentUser: Boolean = false
)

