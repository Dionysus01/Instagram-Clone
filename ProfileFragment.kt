package com.example.instagramclone.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.instagramclone.MainActivity
import com.example.instagramclone.R
import com.example.instagramclone.editProfile
import com.example.instagramclone.model.users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var profileId: String
    private lateinit var firebaseUser:FirebaseUser
    private val urlRealtime:String="https://instagramclone-c1a83-default-rtdb.asia-southeast1.firebasedatabase.app"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_profile,container,false)

        firebaseUser= FirebaseAuth.getInstance().currentUser!!
        val pref=context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(pref!=null)
        {
            this.profileId= pref.getString("profileID","none").toString()
        }
//        check above line
        if (profileId==firebaseUser.uid)
        {
            view.edit_profile_btn.text="Edit Profile"
        }
        else if(profileId!=firebaseUser.uid)
        {
            checkFollowAndFollowing()
        }

        view.edit_profile_btn.setOnClickListener {
            val getBtn=view.edit_profile_btn.text.toString()
            when
            {
                getBtn=="Edit Profile" ->startActivity(Intent(context,editProfile::class.java))

                getBtn=="Follow"->{
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance(urlRealtime).reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .setValue(true)
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance(urlRealtime).reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)
                    }
                }

                getBtn=="Following"->{
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance(urlRealtime).reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance(urlRealtime).reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .removeValue()
                    }
                }

            }

        }

        getFollowers()
        getFollowing()
        userInfo()
        return view
    }

    private fun checkFollowAndFollowing() {
        val followRef=firebaseUser?.uid.let { it1->
            FirebaseDatabase.getInstance(urlRealtime).reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

//        Log.d("check1", "checkFollowAndFollowing: ")
        if (followRef!=null)
        {
            followRef.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
//                    Log.d("check2","onDataChange: ")
                    if (snapshot.child(profileId).exists())
                    {
//                        Log.d("check3","onDataChange: true")
                        view?.edit_profile_btn?.text = "Following"
                    }
                    else{
//                        Log.d("check3","onDataChange: false")
                        view?.edit_profile_btn?.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

    }

    private fun getFollowers()
    {
        val followRef=FirebaseDatabase.getInstance(urlRealtime).reference
                .child("Follow").child(profileId)
                .child("Followers")

        followRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    view?.total_followers?.text=snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getFollowing()
    {
        val followRef=FirebaseDatabase.getInstance(urlRealtime).reference
                .child("Follow").child(profileId)
                .child("Following")

        followRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    view?.total_following?.text=snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun userInfo()
    {
        val userRef=FirebaseDatabase.getInstance(urlRealtime).reference.child("Users")
            .child(profileId)

        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
//                if(context==null)
//                {
//                    return
//                }
                if (snapshot.exists())
                {
                    val user=snapshot.getValue<users>(users::class.java)
                    Picasso.get().load(user!!.getImageUrl())
                        .placeholder(R.drawable.ic_profile).into(view?.pro_img_view)
                    view?.profile_username?.text=user.getUsername()
                    view?.profile_bio?.text=user.getBio()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()
        val pref= context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileID",firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref= context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileID",firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref= context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileID",firebaseUser.uid)
        pref?.apply()
    }
}