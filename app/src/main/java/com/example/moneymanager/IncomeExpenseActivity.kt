package com.example.moneymanager

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class IncomeExpenseActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var spinner: Spinner
    private lateinit var spinnerType: Spinner
    private lateinit var etMemo: EditText
    private lateinit var etAmount: EditText
    private lateinit var array: Array<String>
    val db = DataBaseHandler(this)

    private fun getDateTime(): String? {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun saveData(view: View) {

        if (etMemo.text.toString().isNullOrEmpty()) {

            Toast.makeText(this, getString(R.string.please_fill_fields), Toast.LENGTH_LONG).show()
        } else if (etAmount.text.toString().isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.please_fill_fields), Toast.LENGTH_LONG).show()
        } else {
            val user = User(spinner.selectedItem.toString(), spinnerType.selectedItem.toString(), etMemo.text.toString(), etAmount.text.toString().toInt(), getDateTime().toString())
            db.insertData(user)
            Toast.makeText(this, getString(R.string.success_message), Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_expense)

        spinner = findViewById(R.id.spinner)
        spinnerType = findViewById(R.id.spinnerType)
        etMemo = findViewById(R.id.etMemo)
        etAmount = findViewById(R.id.etAmount)

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

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {

                if (position == 0) {

                    //Data Category Income
                    val data = db.readIncomeCategory()
                    val arrayListIncome: ArrayList<String> = ArrayList()

                    for (i in 0 until data.size) {
                        arrayListIncome.add(data[i].categoryname)
                    }

                    val adapterIncome = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, arrayListIncome)
                    spinnerType.adapter = adapterIncome
                } else {

                    //Data Category Expense
                    val data = db.readExpenseCategory()
                    val arrayListExpense: ArrayList<String> = ArrayList()

                    for (i in 0 until data.size) {
                        arrayListExpense.add(data[i].categoryname)
                    }

                    val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, arrayListExpense)
                    spinnerType.adapter = adapter
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }

        array = arrayOf("Income", "Expense")
        val adapterIncome = ArrayAdapter(this, android.R.layout.simple_spinner_item, array)
        spinner.adapter = adapterIncome
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