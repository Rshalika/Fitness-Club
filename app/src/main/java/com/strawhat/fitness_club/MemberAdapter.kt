package com.strawhat.fitness_club

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.member_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 100
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
}