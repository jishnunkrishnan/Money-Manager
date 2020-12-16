package com.jishnunkrishnan.moneymanager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
class ProfileActivity : AppCompatActivity() {

    private lateinit var etNameProfile : EditText
    private lateinit var etEmailProfile : EditText
    private lateinit var ivProfile: ImageView
    private lateinit var signOutButton: Button
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var account: GoogleSignInAccount
    private lateinit var tvNameNav: TextView
    private lateinit var tvEmailNav: TextView
    private lateinit var ivProfileNav: ImageView
    private var name : String? = null
    private var email: String? = null
    private val db = DataBaseHandler(this)

    override fun onStart() {
        super.onStart()
        account = GoogleSignIn.getLastSignedInAccount(this)!!
    }

    private fun databaseFetch () {

        val model: List <UserProfile> = db.readProfile()
        val empArrayName = Array(model.size){"null"}
        val empArrayEmail = Array(model.size) {"null"}

        for ((index, i) in model.withIndex()) {

            empArrayName[index] = i.name
            empArrayEmail[index] = i.email
            name = empArrayName[0]
            email = empArrayEmail[0]
        }
    }

    fun editProfile(view: View) {

        val name = etNameProfile.text.toString()
        val email = etEmailProfile.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(this,  "Please enter your name", Toast.LENGTH_LONG).show()
        } else {

            val user = UserProfile(name, email)
            //val db = DataBaseHandler(this)
            db.updateProfile(user, 1.toString())
            Toast.makeText(this, "Name updated successfully", Toast.LENGTH_LONG).show()
        }
    }

    //var nameSaved: Boolean = false
    fun gotoProfile(view: View) {
        if (account != null) {
            startActivity(Intent(this, ProfileActivity::class.java))
        } else {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, OnCompleteListener<Void?> {

                Toast.makeText(this, "Sign Out Successfully", Toast.LENGTH_LONG).show()
                finish()
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        title = "Profile"
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        etNameProfile = findViewById(R.id.etNameProfile)
        etEmailProfile = findViewById(R.id.etEmailProfile)

        etEmailProfile.setOnClickListener {
            Toast.makeText(this, "Sorry, you can't edit email!", Toast.LENGTH_LONG).show()
        }

        ivProfile = findViewById(R.id.ivProfile)
        signOutButton = findViewById(R.id.signOutButton)
        signOutButton.setOnClickListener {

            when (it.id) {
                R.id.signOutButton -> signOut()
            }
        }

        /*SideMenu START*/
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        /*SideMenu END*/

        //Menu Button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /* Side Menu Selection START*/
        val navView: NavigationView = findViewById(R.id.navView)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miItem1 -> startActivity(Intent(this, MainActivity::class.java))
                R.id.miItem2 -> startActivity(Intent(this, CategoryActivity::class.java))
                R.id.miItem3 -> startActivity(Intent(this, ExportActivity::class.java))
                R.id.miItem4 -> startActivity(Intent(this, SettingsActivity::class.java))
            }
            true
        }
        /* Side Menu Selection END*/

        //Profile data START
        lateinit var personName: String
        lateinit var personEmail: String
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
             personName = acct.displayName.toString()
            //val personGivenName = acct.givenName
            //val personFamilyName = acct.familyName
            personEmail = acct.email.toString()
            // val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl

            Glide.with(this).load(personPhoto).into(ivProfile)

            //val navView: NavigationView = findViewById(R.id.navView)
            val headerView = navView.getHeaderView(0)
            tvNameNav = headerView.findViewById(R.id.tvNameNav)
            tvEmailNav = headerView.findViewById(R.id.tvEmailNav)
            ivProfileNav = headerView.findViewById(R.id.ivProfileNav)
            tvNameNav.text = personName
            tvEmailNav.text = personEmail
            Glide.with(this).load(personPhoto).into(ivProfileNav)
        }
        //Profile data END

        databaseFetch()

        if(name == null) {

            val userProfile = UserProfile(personName,personEmail)
            db.insertProfile(userProfile)
        }
        databaseFetch()
        etNameProfile.setText(name)
        etEmailProfile.setText(email)
    }

    /* Toggle Menu Start */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    /* Toggle Menu End */
}