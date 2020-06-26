package com.strawhat.fitness_club.view

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.guanaj.easyswipemenulibrary.EasySwipeMenuLayout
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
        holder.time.text = createCustomSpan(context, member.time, R.style.TimeName2)
        if (member.isCurrentUser) {
            (holder.itemView as EasySwipeMenuLayout).isCanLeftSwipe = false
            holder.itemView.isCanRightSwipe = false
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.dark_mint_50
                )
            )
            holder.time.setTextColor(ContextCompat.getColor(context, R.color.turquoise_blue))
            holder.time.text = createCustomSpan(context, member.time, R.style.TimeName3)
        }

        Glide
            .with(context)
            .load(member.image)
            .circleCrop()
            .placeholder(R.drawable.loader_image)
            .into(holder.image)
        holder.permissions.setOnClickListener {
            Toast.makeText(context, R.string.assign_permission, Toast.LENGTH_SHORT).show()
        }
        holder.remove.setOnClickListener {
            Toast.makeText(context, R.string.remove_from_group, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createCustomSpan(context: Context, text: String, appearance: Int): SpannableString {
        val spannableString = SpannableString(text)
        setSpanIfFound(text, spannableString, "სთ", appearance, context)
        setSpanIfFound(text, spannableString, "წთ", appearance, context)
        return spannableString
    }

    private fun setSpanIfFound(text: String, spannableString: SpannableString, token: String, appearance: Int, context: Context) {
        val index = text.indexOf(token)
        if (index >= 0) {
            spannableString.setSpan(
                TextAppearanceSpan(context, appearance),
                index, index + 2,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
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
        val permissions: Button = itemView.findViewById(R.id.assign_permissions_list)
        val remove: Button = itemView.findViewById(R.id.remove_from_group_list)
    }
}