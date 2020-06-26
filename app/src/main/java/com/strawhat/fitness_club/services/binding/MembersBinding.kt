package com.strawhat.fitness_club.services.binding

import com.google.gson.annotations.SerializedName
import com.strawhat.fitness_club.view.ClubMemberViewModel


data class MembersBinding(
    @SerializedName("members")
    var members: List<Member>,

    @SerializedName("hasMore")
    var hasMore: Boolean
)

data class Member(
    @SerializedName("hours")
    var hours: String,
    @SerializedName("id")
    var id: Int,
    @SerializedName("imageUrl")
    var imageUrl: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("position")
    var position: Int
)

fun MembersBinding.toViewModel(): List<ClubMemberViewModel> {
    val res = mutableListOf<ClubMemberViewModel>()
    this.members.forEach {
        res.add(
            ClubMemberViewModel(
                number = it.id,
                name = it.name,
                image = it.imageUrl,
                time = it.hours,
                isCurrentUser = false
            )
        )
    }
    return res
}