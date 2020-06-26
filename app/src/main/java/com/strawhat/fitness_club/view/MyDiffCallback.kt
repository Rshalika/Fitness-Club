package com.strawhat.fitness_club.view

import androidx.recyclerview.widget.DiffUtil

class MyDiffCallback(
    var newMembers: List<ClubMemberViewModel>,
    var oldMembers: List<ClubMemberViewModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldMembers.size
    }

    override fun getNewListSize(): Int {
        return newMembers.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMembers[oldItemPosition].number == newMembers[newItemPosition].number
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMembers[oldItemPosition] == newMembers[newItemPosition]
    }

}