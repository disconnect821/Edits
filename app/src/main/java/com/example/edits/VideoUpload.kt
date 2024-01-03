package com.example.edits

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.bumptech.glide.Glide
import com.example.edits.adapter.VideoViewPagerAdapter
import com.example.edits.databinding.ActivityVideoUploadBinding
import com.example.edits.models.VideoModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

class VideoUpload : AppCompatActivity() {
    private lateinit var binding : ActivityVideoUploadBinding
    private var selectedVideoUri : Uri? = null
    private lateinit var videoLauncher : ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                selectedVideoUri = it.data?.data
                showPostLayout()
            }
        }

        binding.uploadGalleryLayout.setOnClickListener{
            checkPermission()
        }

        binding.cancelPostButton.setOnClickListener{
            finish()
        }

        binding.postButton.setOnClickListener {
            postVideo()
        }
    }

    private fun postVideo() {


        selectedVideoUri?.apply {

            //firebase cloud storage
            val videoRef = FirebaseStorage.getInstance()
                .reference
                .child("video/" + this.lastPathSegment)

            videoRef.putFile(this)
                .addOnSuccessListener {
                    videoRef.downloadUrl.addOnSuccessListener {
                        //Download url of the video
                        postToFireStore(it.toString())
                    }
                }
        }
    }

    private fun postToFireStore(url: String) {
        val videoModel = VideoModel(
            FirebaseAuth.getInstance().currentUser?.uid!! + "_"+ Timestamp.now().toString(),
            binding.captionEditText.text.toString(),
            url,
            FirebaseAuth.getInstance().currentUser?.uid!!,
            Timestamp.now()
        )

        Firebase.firestore.collection("videos")
            .document(videoModel.videoId)
            .set(videoModel)
            .addOnSuccessListener {
                Toast.makeText(this,"Video Uploaded", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener{
                Toast.makeText(this,"Error in uploading", Toast.LENGTH_SHORT).show()
            }
    }


    private fun showPostLayout() {

        selectedVideoUri?.let {
            binding.uploadLayout.visibility = View.GONE
            binding.uploadPostLayout.visibility = View.VISIBLE

            Glide.with(binding.toBeUploadVideo).load(it).into(binding.toBeUploadVideo)
        }
    }

    private fun checkPermission() {
        var readExternalVideo = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            readExternalVideo = android.Manifest.permission.READ_MEDIA_VIDEO
        }else{
            readExternalVideo = android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if(ContextCompat.checkSelfPermission(this, readExternalVideo) == PackageManager.PERMISSION_GRANTED){
            //If we have gallery permission
            openVideo()
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(readExternalVideo),
                100
            )
        }
    }

    @SuppressLint("IntentReset")
    private fun openVideo() {
        val intent = Intent(Intent.ACTION_PICK ,MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/*"
        videoLauncher.launch(intent)
    }


}