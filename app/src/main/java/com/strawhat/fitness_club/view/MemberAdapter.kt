package com.strawhat.fitness_club.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
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
        val context = holder.itemView.context
        holder.number.text = member.number.toString()
        holder.name.text = member.name
        holder.time.text = member.time
        if (member.isCurrentUser) {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.dark_mint_50
                )
            )
            holder.time.setTextColor(ContextCompat.getColor(context, R.color.turquoise_blue))
        }

        Glide
            .with(context)
            .load(member.image)
            .circleCrop()
            .placeholder(R.drawable.loader_image)
            .into(holder.image)
    }

    fun setMembers(newList: List<ClubMemberViewModel>) {
        val diffResult = DiffUtil.calculateDiff(
            MyDiffCallback(this.members, newList)
        )
        members.clear()
        members.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val number: TextView = itemView.findViewById(R.id.number)
        val name: TextView = itemView.findViewById(R.id.profile_name)
        val image: ImageView = itemView.findViewById(R.id.profile_image)
        val time: TextView = itemView.findViewById(R.id.profile_time)
    }
}