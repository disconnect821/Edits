package com.example.edits.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.edits.ProfileVideoPlayerActivity
import com.example.edits.databinding.PostItemViewBinding
import com.example.edits.models.VideoModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class PostVideoAdapter(options: FirestoreRecyclerOptions<VideoModel>) : FirestoreRecyclerAdapter<VideoModel , PostVideoAdapter.VideoViewHolder>(options) {

    inner class VideoViewHolder(private val binding : PostItemViewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(video : VideoModel){
            Glide.with(binding.thumbnailPost).load(video.url).into(binding.thumbnailPost)
            binding.thumbnailPost.setOnClickListener{
                val intent = Intent(binding.thumbnailPost.context, ProfileVideoPlayerActivity::class.java)
                intent.putExtra("videoId", video.videoId)
                binding.thumbnailPost.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = PostItemViewBinding.inflate(LayoutInflater.from(parent.context) ,parent ,false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int, model: VideoModel) {
        holder.bind(model)
    }
}