package com.example.moneymanager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.navigation.NavigationView
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var tvNameNav: TextView
    private lateinit var tvEmailNav: TextView
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var simpleSwitch: Switch
    private lateinit var tvReminderTime: TextView
    private lateinit var ivProfileNav: ImageView
    private var isEnabled: Boolean = false

    fun gotoProfile(view: View) {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun activateNotification() {

        val hour= sharedPreferences.getString("hour", 0.toString())
        val minute = sharedPreferences.getString("minute", 0.toString())
        val second = sharedPreferences.getString("second", 0.toString())

        val calender = Calendar.getInstance()
        if (hour != null) {
            calender[Calendar.HOUR_OF_DAY] = hour.toInt()
        }
        if (minute != null) {
            calender[Calendar.MINUTE] = minute.toInt()
        }
        if (second != null) {
            calender[Calendar.SECOND] = second.toInt()
        }
        val intent = Intent(applicationContext, ReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = (getSystemService(ALARM_SERVICE) as AlarmManager)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calender.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {

        val name: CharSequence = getString(R.string.money_manager_notification)
        val description = getString(R.string.channel_money_manager)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("notifyMe", name, importance)
        channel.description = description
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        createNotificationChannel()

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        tvReminderTime = findViewById(R.id.tvReminderTime)
        simpleSwitch = findViewById(R.id.simpleSwitch)
        simpleSwitch.setOnCheckedChangeListener { _, isChecked ->
            //val message = if (isChecked) "Switch1:ON" else "Switch1:OFF"

            //var mes = ""
            if (isChecked) {
                //mes = "Switch ON"
                activateNotification()

                isEnabled = true

                val hour = 17
                val minute = 14
                val second = 0
                val editor:SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("hour", hour.toString())
                editor.putString("minute", minute.toString())
                editor.putString("second", second.toString())
                editor.putBoolean("isEnabled", isEnabled)
                editor.apply()

                tvReminderTime.text = getString(R.string.fire_notification)

            } else {
                //mes = "Switch Off"
                isEnabled = false
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()
                tvReminderTime.text = getString(R.string.reminder_off)
            }
            //Toast.makeText(this@SettingsActivity, mes, Toast.LENGTH_SHORT).show()
        }
        val isEnabledBoolean = sharedPreferences.getBoolean("isEnabled", false)

        if (isEnabledBoolean) {

            tvReminderTime.text = getString(R.string.fire_notification)
            simpleSwitch.isChecked = true
        } else {
            tvReminderTime.text = getString(R.string.reminder_off)
            simpleSwitch.isChecked = false
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

        //Profile data start
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            //val personGivenName = acct.givenName
            //val personFamilyName = acct.familyName
            val personEmail = acct.email
            // val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl
            //     Glide.with(this).load(personPhoto).into(ivProfile)

            //val navView: NavigationView = findViewById(R.id.navView)
            val headerView = navView.getHeaderView(0)
            tvNameNav = headerView.findViewById(R.id.tvNameNav)
            tvEmailNav = headerView.findViewById(R.id.tvEmailNav)
            ivProfileNav = headerView.findViewById(R.id.ivProfileNav)
            tvNameNav.text = personName
            tvEmailNav.text = personEmail
            Glide.with(this).load(personPhoto).into(ivProfileNav)
        }
        //Profole data end
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