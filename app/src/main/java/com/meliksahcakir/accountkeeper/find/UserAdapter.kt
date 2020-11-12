package com.meliksahcakir.accountkeeper.find

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.UserInfo
import kotlinx.android.synthetic.main.user_view.view.*

class UserAdapter(private val listener: OnClickListener) :
    ListAdapter<UserInfo, UserViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<UserInfo>() {
        override fun areItemsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(parent)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }
}

class UserViewHolder(private val parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.user_view, parent, false)
) {
    private val profileImageView: ImageView by lazy { itemView.userProfileImageView }
    private val userNameTextView: TextView by lazy { itemView.userNameTextView }
    private val userEmailTextView: TextView by lazy { itemView.userEmailTextView }
    private val userCardView: MaterialCardView by lazy { itemView.userCardView }

    fun bind(user: UserInfo, listener: OnClickListener) {
        userNameTextView.text = user.username
        userEmailTextView.text = user.email
        userCardView.setOnClickListener {
            listener.onClick(user)
        }
    }
}

interface OnClickListener {
    fun onClick(user: UserInfo)
}