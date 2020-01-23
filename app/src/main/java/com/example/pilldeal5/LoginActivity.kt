package com.example.pilldeal5

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mGoogleSignInClient : GoogleSignInClient? = null
    private var signInButton : SignInButton? =null
    private var progressBar : ProgressBar? =null

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.

        if (mAuth!!.currentUser != null) {
            val user = mAuth!!.currentUser
            updateUI(user)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signInButton = findViewById(R.id.sign_in_button)
        progressBar = findViewById(R.id.progress_circular)

        // allow offline behaviour
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        // Initialize Firebase Auth





        //Google Authentication------------------------------------------------------------------------------------------------------
        // Google sign in options client!!!!!!!!!!!
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        signInButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Log.i("Tentei Login","Entrou")
                progressBar?.visibility = View.VISIBLE
                val signInIntent = mGoogleSignInClient?.signInIntent
                startActivityForResult(signInIntent, 101)
            }
        })


        if (mAuth!!.currentUser != null) {
            val user = mAuth!!.currentUser
            updateUI(user)
        }

    }

    fun  googleSignin(){

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("onActivityResult","Entrou")
        Log.i("onActivityResult",requestCode.toString())
        Log.i("onActivityResult", resultCode.toString())
        Log.i("onActivityResult",data.toString())




        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) { //if intent showed
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!) //if acount != than null run function
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("", "Google sign in failed", e)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.i("USER","firebaseAuthwithgoogle" + acct.idToken)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        progressBar?.visibility = View.INVISIBLE
                        Log.i("SUCCESS","signin success")

                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth?.currentUser
                        val sharedPref = getSharedPreferences(
                                getString(R.string.preference_file_alarm_time), Context.MODE_PRIVATE)  ?: return@addOnCompleteListener
                        with (sharedPref.edit()) {
                            putString(getString(R.string.preference_file_alarm_time), user.toString())
                            commit()
                        }
                        //Log.i("USER", mAuth?.currentUser?.getIdToken().toString())
                        updateUI(user)
                    } else {
                        progressBar?.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, "Invalid Login",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                    // ...
                }
    }

    private fun updateUI(user: FirebaseUser?) {

        if(user !=null){
            val name = user.displayName
            val email =user.email
            val photo = user.photoUrl.toString()
            Log.i("DataUser",name + email + photo)

            // Write a message to the database
            val database = FirebaseDatabase.getInstance().getReference()

            database.child("users").child(user.uid).child("Email").setValue(name)
            database.child("users").child(user.uid).child("Nome").setValue(email)
            database.child("users").child(user.uid).child("Photo").setValue(photo)

            moveToMainActivity(user.uid)

        }else{
            Log.i("NoUser","no user data")
        }

        //Update database


    }


    private fun moveToMainActivity(user:String) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("user", user) //pass user uid to main activity
        startActivity(intent)
    }

}
