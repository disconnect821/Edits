package com.example.edits

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.edits.adapter.VideoViewPagerAdapter
import com.example.edits.databinding.ActivityMainBinding
import com.example.edits.models.VideoModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var adapter : VideoViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.bottom_menu_home -> {Toast.makeText(this,"Home" , Toast.LENGTH_SHORT).show()}
                R.id.bottom_menu_new_video ->{
                    startActivity(Intent(this,VideoUpload::class.java))
                }
                R.id.bottom_menu_profile ->{
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("profile_user_id", FirebaseAuth.getInstance().currentUser?.uid)
                    startActivity(intent)
                }
            }
            false
        }

        viewPagerSetup()
    }

    private fun viewPagerSetup() {
        val options  = FirestoreRecyclerOptions.Builder<VideoModel>()
            .setQuery(
                Firebase.firestore.collection("videos"),
                VideoModel::class.java
            ).build()

        adapter = VideoViewPagerAdapter(options)
        binding.viewPager.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}