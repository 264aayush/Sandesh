package com.singh.sandesh

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_list_item_layout.view.*


private lateinit var tb:Toolbar;
private lateinit var recyclerView:RecyclerView
private lateinit var database:FirebaseDatabase
private lateinit var adapter: GroupAdapter<GroupieViewHolder>

class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        tb=findViewById(R.id.user_toolbar);
        setSupportActionBar(tb);

        database = Firebase.database

        val name: String? =FirebaseAuth.getInstance().currentUser?.displayName
        supportActionBar?.title =name

        recyclerView=findViewById(R.id.user_activity_recycler_view)
        adapter=GroupAdapter()
        fetchusers()
    }

    private fun fetchusers() {
        val myRef = database.getReference("users")
        myRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                return
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    val u = it.getValue(user::class.java)
                    if(u!=null){
                        if(u.uid!=FirebaseAuth.getInstance().uid){
                            adapter.add(userItem(u))
                        }
                    }
                }
            }
        })
        recyclerView.adapter= adapter;
        adapter.setOnItemClickListener { item, view ->
            var intent=Intent(view.context,ChattingLogsActivity::class.java)
            val useritem=item as userItem
            intent.putExtra("user",useritem.u);
            startActivity(intent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_activity_menu,menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id:Int =item.itemId

        if(id==R.id.sign_out_menu_item){
            FirebaseAuth.getInstance().signOut();
            val intent=Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent);
            return true;
        }
        else
            return super.onOptionsItemSelected(item)
    }
}

