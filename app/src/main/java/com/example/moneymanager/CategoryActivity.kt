package com.example.moneymanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.navigation.NavigationView

class CategoryActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var tvNameNav: TextView
    private lateinit var tvEmailNav: TextView
    private lateinit var listView: ListView
    private lateinit var etCategory: EditText
    private lateinit var tvIncome: TextView
    private lateinit var tvExpense: TextView
    private lateinit var tvCategories: TextView
    private lateinit var tvAddNewCategory: TextView
    private lateinit var ivProfileNav: ImageView
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private var db = DataBaseHandler(this)
    var category: Boolean = true

    fun gotoProfile(view: View) {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun readIncome () {
        val data = db.readIncomeCategory()
        val arrayList: ArrayList<String> = ArrayList()

        arrayList.clear()
        for (i in 0 until data.size) {
            arrayList.add(data[i].categoryname)
        }

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList)
        listView.adapter = arrayAdapter
        tvCategories.text = getString(R.string.income_category)
    }

    private fun readExpense () {
        val data = db.readExpenseCategory()
        val arrayList: ArrayList<String> = ArrayList()

        arrayList.clear()
        for (i in 0 until data.size) {
            arrayList.add(data[i].categoryname)
        }

        arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList)
        listView.adapter = arrayAdapter
        tvCategories.text = getString(R.string.expense_category)
    }

    @SuppressLint("ResourceAsColor")
    fun switchIncomeCategory(view: View) {
        category = true
        tvAddNewCategory.text = getString(R.string.add_income_category)
        tvIncome.setBackgroundResource(R.color.BGTEXTVIEW)
        tvExpense.setBackgroundResource(R.color.BGBLUE)
        tvIncome.setTextColor(Color.BLACK)
        tvExpense.setTextColor(Color.GRAY)
        readIncome()
    }

    @SuppressLint("ResourceAsColor")
    fun switchExpenseCategory(view: View) {

        category = false
        tvAddNewCategory.text = getString(R.string.add_expense_category)
        tvIncome.setBackgroundResource(R.color.BGBLUE)
        tvExpense.setBackgroundResource(R.color.BGTEXTVIEW)
        tvIncome.setTextColor(Color.GRAY)
        tvExpense.setTextColor(Color.BLACK)
        readExpense()
    }

    fun addCategory(view: View) {

        if (category) {
            if (etCategory.text.toString().isEmpty()) {

                etCategory.error = getString(R.string.please_enter_category)
            } else {
                val userCategory = UserCategory(getString(R.string.income), etCategory.text.toString())
                val db = DataBaseHandler(this)
                db.insertCategory(userCategory)
                Toast.makeText(this, getString(R.string.success_message), Toast.LENGTH_LONG).show()
                readIncome()
            }
        } else {

            if (etCategory.text.toString().isEmpty()) {

                etCategory.error = getString(R.string.please_fill_fields)
            } else {
                val userCategory = UserCategory(getString(R.string.expense), etCategory.text.toString())
                val db = DataBaseHandler(this)
                db.insertCategory(userCategory)
                Toast.makeText(this, getString(R.string.success_message), Toast.LENGTH_LONG).show()
                readExpense()
            }
        }
        etCategory.text = null
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        title = getString(R.string.title_categories)
        tvAddNewCategory = findViewById(R.id.tvAddNewCategory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        tvIncome = findViewById(R.id.tvIncome)
        tvExpense = findViewById(R.id.tvExpense)
        tvCategories = findViewById(R.id.tvCategories)
        tvIncome.setBackgroundResource(R.color.BGTEXTVIEW)
        tvExpense.setBackgroundResource(R.color.BGBLUE)
        category = true
        listView = findViewById(R.id.listView)
        etCategory = findViewById(R.id.etCategory)
        readIncome()
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