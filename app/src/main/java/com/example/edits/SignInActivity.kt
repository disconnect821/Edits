package com.example.edits

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.edits.databinding.ActivitySignInBinding
import com.example.edits.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class SignInActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.logInhereText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.validateButton.setOnClickListener{
            validateUser()
        }
    }

    private fun validateUser() {
        val email  = binding.inputEmail.text.toString()
        val password  = binding.inputPassword.text.toString()
        val confirmPassword  = binding.inputConfirmPassword.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.inputEmail.error = "Email not valid"
            return
        }
        if(password.length<8){
            binding.inputPassword.error = "Too short!"
            return
        }
        if(password != confirmPassword){
            binding.inputConfirmPassword.error = "Password not matched"
            return
        }
        val userName  = binding.inputUsername.text.toString()
        signIn(email, password, userName)
    }

    private fun signIn(email : String, password : String , userName : String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                it.user?.let { user ->
                    val userModel = User(user.uid, email, userName)
                    Firebase.firestore.collection("users")
                        .document(user.uid)
                        .set(userModel).addOnSuccessListener {
                            Toast.makeText(applicationContext ,"SignIn Success full" , Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                }
            }.addOnFailureListener{
                Toast.makeText(applicationContext ,"Error Signing In" , Toast.LENGTH_SHORT).show()
            }
    }
}