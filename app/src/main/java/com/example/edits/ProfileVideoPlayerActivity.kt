package com.example.edits

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.edits.adapter.VideoViewPagerAdapter
import com.example.edits.databinding.ActivityProfileVideoPlayerBinding
import com.example.edits.models.VideoModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ProfileVideoPlayerActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileVideoPlayerBinding
    lateinit var videoId : String
    lateinit var adapter : VideoViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoId = intent.getStringExtra("videoId")!!
        setupViewPager()
    }

    private fun setupViewPager() {
        val options = FirestoreRecyclerOptions.Builder<VideoModel>()
            .setQuery(
                Firebase.firestore.collection("videos")
                    .whereEqualTo("videoId",videoId),
                VideoModel::class.java
            ).build()
        adapter = VideoViewPagerAdapter(options)
        binding.profileVideoPlayer.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }
}