package com.example.edits

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.edits.adapter.PostVideoAdapter
import com.example.edits.databinding.ActivityProfileBinding
import com.example.edits.models.User
import com.example.edits.models.VideoModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    private lateinit var profileUserId : String
    private lateinit var currentUserId : String
    private lateinit var profileUserModel : User
    private lateinit var photoLauncher : ActivityResultLauncher<Intent>
    private lateinit var postVideoAdapter: PostVideoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        profileUserId = intent.getStringExtra("profile_user_id")!!
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!

        if(profileUserId == currentUserId){
            binding.logoutButton.text = "logout"
            binding.logoutButton.setOnClickListener{
                logout()
            }
            binding.profilePic.setOnClickListener{
                checkPermissionAndPickPhoto()
            }
        }else{
            binding.logoutButton.text = "Follow"
            binding.logoutButton.setOnClickListener {
                followOrUnfollow()
            }
        }
        getProfileData()

        photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                it.data?.data?.let { it1 -> uploadToFireStore(it1) }!!
            }
        }

        setupPostForProfileRecyclerView()
    }

    private fun setupPostForProfileRecyclerView() {
        val option = FirestoreRecyclerOptions.Builder<VideoModel>()
            .setQuery(
                Firebase.firestore.collection("videos")
                    .whereEqualTo("uploaderId",profileUserId)
                    .orderBy("createdTime" , Query.Direction.DESCENDING),
                VideoModel::class.java
            ).build()
        postVideoAdapter = PostVideoAdapter(option)
        binding.postRecyclerView.adapter = postVideoAdapter
    }

    private fun followOrUnfollow() {
        Firebase.firestore.collection("users")
            .document(currentUserId)
            .get()
            .addOnSuccessListener {
                val currentUserModel = it.toObject(User::class.java)!!
                if(profileUserModel.followList.contains(currentUserId)){
                    //unfollow user
                    profileUserModel.followList.remove(currentUserId)
                    currentUserModel.followingList.remove(profileUserId)
                    binding.logoutButton.text = "Follow"
                }else{
                    profileUserModel.followList.add(currentUserId)
                    currentUserModel.followingList.add(profileUserId)
                    binding.logoutButton.text = "unfollow"
                }
                updateUserData(profileUserModel)
                updateUserData(currentUserModel)
            }
    }

    private fun updateUserData(model : User) {
        Firebase.firestore.collection("users")
            .document(model.id)
            .set(model)
            .addOnSuccessListener {
                getProfileData()
            }
    }

    private fun uploadToFireStore(photoUri: Uri) {
        val photoRef = FirebaseStorage.getInstance()
            .reference
            .child("profilePic/" + currentUserId)

        photoRef.putFile(photoUri)
            .addOnSuccessListener {
                photoRef.downloadUrl.addOnSuccessListener {
                    //Download url of the video
                    postToFireStore(it.toString())
                }
            }
    }

    private fun postToFireStore(url: String) {
        Firebase.firestore.collection("users")
            .document(currentUserId)
            .update("profilePic" , url)
            .addOnSuccessListener {
                Toast.makeText(this,"Profile picture changed", Toast.LENGTH_SHORT).show()
                getProfileData()
            }
            .addOnFailureListener{
                Toast.makeText(this,"Error in uploading", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkPermissionAndPickPhoto() {
        var readExternalPhoto = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            readExternalPhoto = android.Manifest.permission.READ_MEDIA_IMAGES
        }else{
            readExternalPhoto = android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if(ContextCompat.checkSelfPermission(this, readExternalPhoto) == PackageManager.PERMISSION_GRANTED){
            //If we have gallery permission
            openPhoto()
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(readExternalPhoto),
                100
            )
        }
    }

    @SuppressLint("IntentReset")
    private fun openPhoto() {
        val intent = Intent(Intent.ACTION_PICK , MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        photoLauncher.launch(intent)
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this,LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    @SuppressLint("RestrictedApi")
    private fun getProfileData() {

        Firebase.firestore.collection("users")
            .document(profileUserId)
            .get()
            .addOnSuccessListener {
                profileUserModel = it.toObject(User::class.java)!!
                Log.d("User data" , profileUserModel.toString())
                setUI()
            }
    }

    private fun setUI() {
        profileUserModel.apply {
            Glide.with(binding.profilePic).load(profilePic)
                .apply(RequestOptions().placeholder(R.drawable.baseline_account_circle_24))
                .circleCrop()
                .into(binding.profilePic)

            binding.username.text = "@$username"
            if(profileUserModel.followList.contains(currentUserId)){
                binding.logoutButton.text = "unfollow"
            }
            binding.followersCount.text = followList.size.toString()
            binding.followingCount.text = followingList.size.toString()

            Firebase.firestore.collection("videos")
                .whereEqualTo("uploaderId",profileUserId)
                .get().addOnSuccessListener{
                    binding.postCount.text = it.size().toString()
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        postVideoAdapter.stopListening()
    }
    

    override fun onStart() {
        super.onStart()
        postVideoAdapter.startListening()
    }
}