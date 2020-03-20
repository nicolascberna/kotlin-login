package com.example.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_change_pass.*

class ChangePassActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pass)

        auth = FirebaseAuth.getInstance()

        sendRequest.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword () {
        val email = changePass.text.toString()

        if (email.isEmpty()) Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show()

        else {
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful){
                    Log.d("PasswordReset", "sendPasswordResetEmail: Succes")
                    Toast.makeText(this, "Email has been sent", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("PasswordReset", "sendPasswordResetEmail: Failed", it.exception)

                    val error = it.exception?.message
                    Toast.makeText(this, "Authentication failed: $error",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
