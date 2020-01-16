package com.example.pilldeal5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.example.pilldeal5.ui.settings.SettingsFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity() {

    private var mAppBarConfiguration: AppBarConfiguration? = null
    private var mAuth: FirebaseAuth? = null
    private var mGoogleSignInClient : GoogleSignInClient? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings, R.id.nav_medinfo,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)



    }

    //INFLATE THE RIGHT HAND SIDE MENU
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    //RIGHT HAND SIDE MENU CLICK ACTIONS
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item?.itemId==R.id.action_logout){

            FirebaseAuth.getInstance().signOut()
            mGoogleSignInClient?.signOut()?.addOnCompleteListener(this
            ) { task -> updateUI(null) }

            //Signout the user
            //move to login
            val intent = Intent (this, Login::class.java)
            startActivity(intent)
        }else if (item?.itemId==R.id.action_reconect){
            //reconect the user
            //finish()
            val intent = Intent (this, reconnect::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun updateUI(user: FirebaseUser?) {

        if(user !=null){
            val name = user.displayName
            val email =user.email
            val photo = user.photoUrl.toString()
            Log.i("DataUser",name + email + photo)

        }else{
            user.equals(null)
            Log.i("NoUser","no user data")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, mAppBarConfiguration!!) || super.onSupportNavigateUp()
    }


}
