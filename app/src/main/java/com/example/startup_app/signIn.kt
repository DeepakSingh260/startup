package com.example.startup_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class signIn : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var signin: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = Firebase.auth

        email = findViewById(R.id.edit_mail)
        password = findViewById(R.id.edit_password)

        signin = findViewById(R.id.button)

        signin.setOnClickListener( object : View.OnClickListener{

            override fun onClick(v: View?) {

                if (v != null){
                    signInfunc(email.text.toString().trim() , password.text.toString().trim())
                }
            }
        })
    }

    private fun signInfunc(email:String , password:String){

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){
                task ->
            if (task.isSuccessful){
                Log.d(TAG, "sign in with email")
                val user = auth.currentUser
                updateUI(user)
                startActivity(Intent(this , MainActivity::class.java))

            }else{
                Log.w(TAG, "sign in failure " , task.exception)
                updateUI(null)
            }
        }


    }
    private fun updateUI(user : FirebaseUser?){

    }

    private fun sendEmailVerification(){

        val user = auth.currentUser

        user.sendEmailVerification().addOnCompleteListener(this){
                task ->
        }
    }
    companion object{
        private const val TAG = "SignIn"

    }
}