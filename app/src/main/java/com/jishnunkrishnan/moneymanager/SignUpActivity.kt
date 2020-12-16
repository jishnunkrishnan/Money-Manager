package com.jishnunkrishnan.moneymanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView

class SignUpActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var signInButton: SignInButton
    private lateinit var mGoogleSignInClient: GoogleSignInClient
   // private lateinit var loginButtonFacebook: LoginButton
   // private lateinit var imageVIew: ImageView
    private var RC_SIGN_IN: Int = 0
   // private var FC_SIGN_IN: Int = 1
    private lateinit var callBackManager: CallbackManager

    fun gotoProfile(view: View) {

        startActivity(Intent(this, SignUpActivity::class.java))
    }

    /*Google Signin APi Start 1*/
    private fun signIn() {

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            //updateUI(account)
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.statusCode)
            //updateUI(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else {

           // callBackManager.onActivityResult(requestCode, resultCode, data)

        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            signIn()
        }
    }
    /*Google sign in api end 1*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        /*Google sign in api start 1*/
        signInButton = findViewById(R.id.signInButton)
        signInButton.setOnClickListener {

            when (it.id) {
                R.id.signInButton -> signIn()
            }
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        /*Google sign in api end 1*/

        /*FAcebook sign in api Start 1*/
       /* //loginButtonFacebook = findViewById(R.id.loginButtonFacebook)
        imageVIew = findViewById(R.id.imageView)
        callBackManager = CallbackManager.Factory.create()

        loginButtonFacebook.setReadPermissions(Arrays.asList("name", "email"))
        loginButtonFacebook.registerCallback(callBackManager, object :
                FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

                val graphRequest = GraphRequest.newMeRequest(loginResult.accessToken) { obj, response ->

                    try {
                        if(obj.has("id ")) {

                            Log.i("Face", obj.getString("name"))
                            Log.i("Face", obj.getString("email"))
                            Log.i("Face", JSONObject(obj.getString("picture")).getJSONObject("data").getString("url"))
                        }
                    } catch (e: Exception) {

                    }
                }
                val param = Bundle()
                param.putString("fields", "name,email,id,picture.type(large)")
                graphRequest.parameters = param
                graphRequest.executeAsync()

                Log.i("Face UserId", loginResult.accessToken.userId)
                //Log.i("Face name", loginResult.accessToken.)
                val imgUrl: String =
                        "https://graph.facebook.com/" + loginResult.accessToken.userId + "/picture?return_ssl_resources=1"
                Picasso.get().load(imgUrl).into(imageVIew)

            }
            override fun onCancel() {
            }
            override fun onError(error: FacebookException) {
            }
        })
        /*Facebook sign in api end 1*/

        */

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