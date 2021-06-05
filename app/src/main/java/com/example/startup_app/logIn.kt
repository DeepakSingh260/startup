package com.example.startup_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class logIn : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var registerButton: Button
    private lateinit var btnLogin: Button
    private lateinit var lemail: EditText
    private lateinit var lpassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            startActivity(Intent(this , MainActivity::class.java))
        }

        registerButton = findViewById(R.id.registerbtn)
        btnLogin = findViewById(R.id.loginbtn)
        lemail = findViewById(R.id.loginEmail)
        lpassword = findViewById(R.id.loginPassword)

        registerButton.setOnClickListener(object: View.OnClickListener {

            override fun onClick(v: View?) {
                startActivity(Intent(this@logIn,signIn::class.java))
            }
        })
        btnLogin.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {
                val email = lemail.getText().toString();
                val password = lpassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

//                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@logIn) { task->


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
//                                progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length < 6) {
                                lpassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            startActivity(Intent(this@logIn , MainActivity::class.java));
                            finish();
                        }
                    }
            }

        })

    }
}