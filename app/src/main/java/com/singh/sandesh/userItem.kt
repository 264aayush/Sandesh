package com.singh.sandesh

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_list_item_layout.view.*

class userItem(val u:user): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_list_item_layout
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.user_item_name.text= u.name
    }


}