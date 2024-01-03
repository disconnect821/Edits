package com.example.edits.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.edits.ProfileActivity
import com.example.edits.R
import com.example.edits.databinding.VideoItemBinding
import com.example.edits.models.User
import com.example.edits.models.VideoModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class VideoViewPagerAdapter (options : FirestoreRecyclerOptions<VideoModel>) : FirestoreRecyclerAdapter<VideoModel, VideoViewPagerAdapter.VideoViewHolder>(options) {
    inner class VideoViewHolder(private val binding : VideoItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(videoModel : VideoModel){

            Firebase.firestore.collection("users")
                .document(videoModel.uploaderId)
                .get().addOnSuccessListener {
                    val userModel = it?.toObject(User::class.java)
                    userModel?.apply {
                        binding.username.text = username
                        Glide.with(binding.profileImage).load(profilePic)
                            .circleCrop()
                            .apply(
                                RequestOptions().placeholder(R.drawable.baseline_person_pin_24)
                            )
                            .into(binding.profileImage)

                        binding.username.setOnClickListener {
                            val intent = Intent(binding.userDetailLayout.context, ProfileActivity::class.java)
                            intent.putExtra("profile_user_id", id)
                            binding.userDetailLayout.context.startActivity(intent)
                        }
                        binding.profileImage.setOnClickListener{
                            val intent = Intent(binding.userDetailLayout.context, ProfileActivity::class.java)
                            intent.putExtra("profile_user_id", id)
                            binding.userDetailLayout.context.startActivity(intent)
                        }
                    }
                }
            binding.caption.text = videoModel.title
            binding.videoProgressBar.visibility = View.VISIBLE


            binding.videoView.apply {
                this.setVideoPath(videoModel.url)
                setOnPreparedListener{
                    binding.videoProgressBar.visibility = View.GONE
                    it.start()
                    it.isLooping = true
                }
                setOnClickListener{
                    if(isPlaying){
                        pause()
                        binding.pauseButton.visibility = View.VISIBLE
                    }else{
                        start()
                        binding.pauseButton.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding  = VideoItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int, model: VideoModel) {
        holder.bind(model)
    }
}