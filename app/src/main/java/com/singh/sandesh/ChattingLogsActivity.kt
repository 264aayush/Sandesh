package com.singh.sandesh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chatting_logs.*
import kotlinx.android.synthetic.main.receive_message_layout.view.*
import kotlinx.android.synthetic.main.send_message_layout.view.*

private lateinit var adapter:GroupAdapter<GroupieViewHolder>
private lateinit var myChildeventLisnter:ChildEventListener
private lateinit var u:user
class ChattingLogsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting_logs)
        
        val tb:Toolbar=findViewById(R.id.chatlog_toolbar)
        setSupportActionBar(tb)

        u =intent.getParcelableExtra<user>("user")!!
        supportActionBar?.title= u.name
        chatting_log_message_edittext.inputType=InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

        adapter= GroupAdapter()
        chatting_log_recyclerview.adapter= adapter

        chatting_log_send_button.setOnClickListener {
            val message_to_send=chatting_log_message_edittext.text.toString()
            val m=message(FirebaseAuth.getInstance().uid!!,u.uid,message_to_send)
            Firebase.database.getReference("messages")
                .child(FirebaseAuth.getInstance().uid!!).child(u.uid)
                .push()
                .setValue(m)
            Firebase.database.getReference("messages")
                .child(u.uid).child(FirebaseAuth.getInstance().uid!!)
                .push()
                .setValue(m)
            chatting_log_message_edittext.setText("")
        }

        myChildeventLisnter=object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val m=p0.getValue(message::class.java)
                if(m?.fromid==FirebaseAuth.getInstance().uid)
                    adapter.add(chat_to(m!!))
                else
                    adapter.add(chat_from(m!!))
                chatting_log_recyclerview.scrollToPosition(adapter.itemCount-1)
            }


            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}

        }



        Firebase.database.getReference("messages")
            .child(u.uid)
            .child(FirebaseAuth.getInstance().uid!!)
            .addChildEventListener(myChildeventLisnter)
    }

    override fun onDestroy() {
        Firebase.database.getReference("messages")
            .child(u.uid)
            .child(FirebaseAuth.getInstance().uid!!)
            .removeEventListener(myChildeventLisnter)
        super.onDestroy()
    }
}

class chat_from(val m: message): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.send_message_layout;
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.send_message_layout_message.text=m.msg
//        viewHolder.itemView.from_user_image=
    }

}
class chat_to(val m:message):Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.receive_message_layout
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.receive_message_layout_message.text=m.msg
//        viewHolder.itemView.to_user_image
    }

}

data class message(val fromid:String,val toid:String,val msg:String){
    constructor():this("","","")
}