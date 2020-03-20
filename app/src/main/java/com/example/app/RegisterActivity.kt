package com.example.app

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var dataReference : DatabaseReference
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dataReference = database.reference.child("User")

        registerButton.setOnClickListener {
            createAccount()
        }

        selectPhoto.setOnClickListener {
            pickImageFromGallery()
        }
    }
    private fun createAccount() {

        val email = userEmail.text.toString().trim()
        val password = userPass.text.toString().trim()
        val userName = userName.text.toString().trim()

        Log.d("Register", "Email is: $email")
        Log.d("Register", "Password is: $password")
        Log.d("Register", "Username is: $userName")


        if (TextUtils.isEmpty(email) || password.isEmpty()) {
            Toast.makeText(this, "Please enter your email and password",
                Toast.LENGTH_SHORT).show()
        } else if (userName.isEmpty()) {
            Toast.makeText(
                this, "Please enter your user name",
                Toast.LENGTH_SHORT).show()
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("CreateUser", "createUserWithEmail: succes")

                        val user:FirebaseUser? = auth.currentUser
                        verifyEmail(user)

                        val userDataBase = dataReference.child(user?.uid!!)
                        userDataBase.child("Name").setValue(userName)

                        uploadImageToFirebaseStorage()
                    } else {
                        Log.w("CreateUser", "createUserWithEmail:failed",
                            task.exception)

                        val error = task.exception?.message
                        Toast.makeText(this, "Authentication failed: $error",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun verifyEmail(user: FirebaseUser?) {
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Cannot send email", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedPhoto = data?.data

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            circleImageViewSelectPhoto.setImageURI(selectedPhoto)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        val filename = UUID.randomUUID().toString()
        val storageReference = FirebaseStorage.getInstance().getReference("/images/$filename")

        //storageReference.putFile()

    }
}
