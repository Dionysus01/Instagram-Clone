package com.example.instagramclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Adapter.PostAdapter
import com.example.instagramclone.R
import com.example.instagramclone.model.posts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var postAdapter:PostAdapter?=null
    private var postList:MutableList<posts>?=null
    private var followingList:MutableList<posts>?=null
    private val urlRealtime: String =
        "https://instagramclone-c1a83-default-rtdb.asia-southeast1.firebasedatabase.app"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_home,container,false)

        var recyclerView:RecyclerView?=null
        recyclerView=view.findViewById(R.id.recycler_view_home)

        var linearLayoutManager=LinearLayoutManager(context)
        linearLayoutManager.reverseLayout=true
        linearLayoutManager.stackFromEnd=true
        recyclerView.layoutManager=linearLayoutManager

        postList=ArrayList()
        postAdapter=context?.let { PostAdapter(it,postList as ArrayList<posts>) }
        recyclerView.adapter=postAdapter

        checkFollowing()

        return view
    }

    private fun checkFollowing() {
        followingList=ArrayList()
        val followingRef=FirebaseDatabase.getInstance(urlRealtime).reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        followingRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    (followingList as ArrayList<String>).clear()
                    for (snapshot in p0.children)
                    {
                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                    readPosts()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun readPosts() {
        val postRef=FirebaseDatabase.getInstance(urlRealtime).reference
            .child("Posts")

        postRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()

                for(snapshots in snapshot.children)
                {
                    val post=snapshots.getValue(posts::class.java)
                    for(id in (followingList as ArrayList<String>))
                    {
                        if(post!!.getpublisher()==id)
                        {
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}