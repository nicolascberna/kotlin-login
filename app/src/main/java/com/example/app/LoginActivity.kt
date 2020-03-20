package com.example.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var firebaseData : FirebaseDatabase
    private lateinit var firebaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            signIn()
            getData()
        }

        newAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotText.setOnClickListener {
            val intent = Intent(this, ChangePassActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn() {

        val email = userEmail.text.toString()
        val password = userPass.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty",
                Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("SignIn", "signInWithEmail:succes")

                        val user: FirebaseUser? = auth.currentUser!!

                        if (user!!.isEmailVerified) {
                            Log.d("SignIn", "isEmailVerified: succes")

                            startActivity(Intent(this, MainActivity::class.java))
                            return@addOnCompleteListener
                        } else {
                            Toast.makeText(this, "Please verify your email account",
                                Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Log.w("SignIn", "signInWithEmail:failure", it.exception)

                        val error = it.exception?.message
                        Toast.makeText(this, "Authentication failed: $error",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun getData() {

        firebaseData = FirebaseDatabase.getInstance()
        firebaseReference = firebaseData.getReference().child("User").child(auth.currentUser!!.uid)


        firebaseReference.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val nameValue = dataSnapshot.child("Name").getValue().toString()
                Log.d("AddValue", "Value is: $nameValue")

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("Name", nameValue)
                //start intent somehow
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("AddValue", "Failed to read value.", error.toException())
            }
        })
    }
}
