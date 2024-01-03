package com.example.edits

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.edits.databinding.ActivityLoginBinding
import com.example.edits.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class LoginActivity : AppCompatActivity() {
    private lateinit var  binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseAuth.getInstance().currentUser?.let {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener{
            validateUser()
        }
        binding.signIntext.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }
    }
    private fun validateUser() {
        val email  = binding.inputEmail.text.toString()
        val password  = binding.inputPassword.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.inputEmail.error = "Email not valid"
            return
        }
        if(password.length<8){
            binding.inputPassword.error = "Too short!"
            return
        }
        logIn(email, password)
    }

    private fun logIn(email : String, password : String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(applicationContext ,"Logged In succesfully" , Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.addOnFailureListener{
                Toast.makeText(applicationContext ,"Error Signing In" , Toast.LENGTH_SHORT).show()
            }
    }
}