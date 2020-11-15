package com.meliksahcakir.accountkeeper.find

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.UserInfo
import com.meliksahcakir.accountkeeper.view.UserCardView
import kotlinx.android.synthetic.main.user_adapter_view.view.*

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
    LayoutInflater.from(parent.context).inflate(R.layout.user_adapter_view, parent, false)
) {
    private val userCardView: UserCardView by lazy { itemView.userCardView }

    fun bind(user: UserInfo, listener: OnClickListener) {
        userCardView.title = user.username
        userCardView.subTitle = user.email
        userCardView.setOnClickListener {
            listener.onClick(user)
        }
    }
}

interface OnClickListener {
    fun onClick(user: UserInfo)
}