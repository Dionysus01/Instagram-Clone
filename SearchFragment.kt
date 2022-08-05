package com.example.instagramclone.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Adapter.UserAdapter
import com.example.instagramclone.R
import com.example.instagramclone.editProfile
import com.example.instagramclone.model.users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var recyclerView:RecyclerView?=null
    private var userAdapter:UserAdapter?=null
    private var mUser:MutableList<users>?=null
    private val urlRealtime:String="https://instagramclone-c1a83-default-rtdb.asia-southeast1.firebasedatabase.app"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_search,container,false)

        recyclerView=view.findViewById(R.id.recycler_view_users)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager=LinearLayoutManager(context)

        mUser=ArrayList()
        userAdapter=context?.let { UserAdapter(it,mUser as ArrayList<users>,true) }
        recyclerView?.adapter=userAdapter

        view.search_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(view.search_edit_text.text.toString()=="")
                {

                }
                else
                {
                    recycler_view_users?.visibility=View.VISIBLE

                    retrieveUsers()
                    searchUser(s.toString().toLowerCase())
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        return view
    }

    private fun searchUser(input: String) {

        val query=FirebaseDatabase.getInstance(urlRealtime).reference
            .child("Users")
            .orderByChild("name")
            .startAt(input)
            .endAt(input+"\uf8ff")

        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                mUser?.clear()

                for(snapshot in datasnapshot.children )
                {
                    val user=snapshot.getValue((users::class.java))
                    if(user!=null)
                    {
                        mUser?.add(user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun retrieveUsers()
    {
        val userRef=FirebaseDatabase.getInstance(urlRealtime).reference.child("Users")
        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if(view?.search_edit_text?.text.toString()=="")
                {
                    mUser?.clear()

                    for(snapshot in datasnapshot.children )
                    {
                        val user=snapshot.getValue((users::class.java))
                        if(user!=null)
                        {
                            mUser?.add(user)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}