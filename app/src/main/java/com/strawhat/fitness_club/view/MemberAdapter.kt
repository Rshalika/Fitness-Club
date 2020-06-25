package com.strawhat.fitness_club.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.strawhat.fitness_club.R

class MemberAdapter(private val members: MutableList<ClubMemberViewModel>) :
    RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.member_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return members.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]
        holder.number.text = "" + (position + 1)
        holder.name.text = member.name
        holder.time.text = member.time

        Glide
            .with(holder.itemView.context)
            .load(member.image)
            .circleCrop()
            .placeholder(R.drawable.loader_image)
            .into(holder.image)
    }

    fun setMembers(members: List<ClubMemberViewModel>) {
        this.members.clear()
        this.members.addAll(members)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val number: TextView = itemView.findViewById(R.id.number)
        val name: TextView = itemView.findViewById(R.id.profile_name)
        val image: ImageView = itemView.findViewById(R.id.profile_image)
        val time: TextView = itemView.findViewById(R.id.profile_time)
    }
}