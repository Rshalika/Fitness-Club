package com.strawhat.fitness_club.services.binding

import com.google.gson.annotations.SerializedName
import com.strawhat.fitness_club.view.ClubInfoViewModel

data class ClubInfoBinding(
    @SerializedName("imageUrl")
    var imageUrl: String,
    @SerializedName("info")
    var info: List<Info>,
    @SerializedName("me")
    var me: Me,
    @SerializedName("name")
    var name: String
)

data class Info(
    @SerializedName("key")
    var key: String,
    @SerializedName("value")
    var value: String
)

data class Me(
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

fun ClubInfoBinding.toViewModel(): ClubInfoViewModel {
    val members = this.info.find { it.key == "წევრი" }?.value?.toInt()!!
    val avgTime = this.info.find { it.key == "საშ. დრო" }?.value!!
    val totalTime = this.info.find { it.key == "სულ დრო" }?.value!!
    return ClubInfoViewModel(
        imageUrl = this.imageUrl,
        clubName = this.name,
        members = members,
        avgTime = avgTime,
        totalTime = totalTime,
        myImageUrl = this.me.imageUrl,
        myId = this.me.id,
        myPosition = this.me.position,
        myName = this.me.name,
        myHours = this.me.hours
    )
}