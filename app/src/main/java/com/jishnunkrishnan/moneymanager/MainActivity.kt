package com.jishnunkrishnan.moneymanager

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private val REQUEST_WRITE_STORAGE = 112
    private lateinit var endDa: String
    private lateinit var toggle: ActionBarDrawerToggle
    private var db = DataBaseHandler(this)
    private lateinit var tvMonth: TextView
    private lateinit var tvIncome: TextView
    private lateinit var tvExpense: TextView
    private lateinit var tvBalance: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var tvNameNav: TextView
    private lateinit var tvEmailNav: TextView
    private lateinit var ivProfileNav: ImageView
    private lateinit var tvChart: TextView
    private lateinit var tvTransactions: TextView
    private lateinit var tvNoTransactions: TextView
    private lateinit var tvPercentage: TextView
    private lateinit var line2: View
    private lateinit var sharedPreferences: SharedPreferences

    private var visitors = ArrayList<PieEntry>()
    private lateinit var pieChartIncome : PieChart

    private var visitorsExpense = ArrayList<PieEntry>()
    private lateinit var pieChartExpense : PieChart

    fun gotoProfile(view: View) {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    /* Calender START */
    fun calenderFunction(view: View) {

        if(calendarView.visibility == View.VISIBLE) {

            calendarView.visibility = View.GONE

            if (tvIncome.text == "0" || tvExpense.text == "0" || tvBalance.text == "0") {
                tvNoTransactions.visibility = View.VISIBLE
            } else {

                tvNoTransactions.visibility = View.GONE
            }

        } else if (calendarView.visibility == View.GONE || calendarView.visibility == View.INVISIBLE) {
            calendarView.visibility = View.VISIBLE
            tvNoTransactions.visibility = View.GONE
        }
    }
    /* Calender END */

    /* Intent to Add Inc & Exp START */
    fun addData(view: View) {

        startActivity(Intent(this, IncomeExpenseActivity::class.java))
    }
    /* Intent to Add Inc & Exp END */

    private fun databaseCategorySetup () {

        //Start
        val incomeCategory = arrayOf("Salary", "Bonus", "Increment")
        val expenseCategory = arrayOf("House Rent", "Mobile Bill", "Fuel")

        for (element in incomeCategory) {

            val userCategory = UserCategory("Income", element)
            val db = DataBaseHandler(this)
            db.insertCategory(userCategory)
        }
        //for (i in 0 until expenseCategory.size) {
        for (element in expenseCategory) {

            val userCategory = UserCategory("Expense", element)
            val db = DataBaseHandler(this)
            db.insertCategory(userCategory)
        }
        //End
    }

    //Getting the days count of each month START
    private fun monthData(data: String) {
        when (data) {
            "1" -> {
                endDa = "31"
            }
            "2" -> {
                endDa = "28"
            }
            "3" -> {
                endDa = "31"
            }
            "4" -> {
                endDa = "30"
            }
            "5" -> {
                endDa = "31"
            }
            "6" -> {
                endDa = "30"
            }
            "7" -> {
                endDa = "31"
            }
            "8" -> {
                endDa = "31"
            }
            "9" -> {
                endDa = "30"
            }
            "10" -> {
                endDa = "31"
            }
            "11" -> {
                endDa = "30"
            }
            "12" -> {
                endDa = "31"
            }
        }
    }
    //Getting the days count of each month END

    private fun listViewData(s: CharSequence, e: CharSequence) {

        val data = db.getDataDate(s, e)
        val empArrayMemo = Array(data.size) { "null" }
        val empArrayIncExp = Array(data.size) { "null" }
        val empArrayAmount = Array(data.size) { "null" }

        for ((index, i) in data.withIndex()) {
            empArrayMemo[index] = i.memo
            empArrayIncExp[index] = i.incomeexpense
            empArrayAmount[index] = i.amount.toString()
        }
        val arrayAdapter = MyListAdapter(this, empArrayMemo, empArrayIncExp, empArrayAmount)
        mainListView.adapter = arrayAdapter

        if(data.isEmpty()) {

            tvTransactions.visibility = View.GONE
            tvChart.visibility = View.GONE
            line2.visibility = View.GONE
            tvNoTransactions.visibility = View.VISIBLE
            tvPercentage.visibility = View.GONE
        } else {

            tvTransactions.visibility = View.VISIBLE
            tvChart.visibility = View.VISIBLE
            line2.visibility = View.VISIBLE
            tvNoTransactions.visibility = View.GONE
            tvPercentage.visibility = View.VISIBLE
        }
    }

    private fun incomeExpenseData(startDate: CharSequence, endDate: CharSequence) {

        /* Getting & Setting Inc, Exp & Balance START */
        //Incom data
        val dataInc = db.readInc(startDate, endDate)
        var incAmount = 0
        for (i in 0 until dataInc.size) {

            incAmount += dataInc[i].amount
        }
        tvIncome.text = incAmount.toString()

        //Exp DAta
        val dataExp = db.readExp(startDate, endDate)
        var expAmount = 0
        for (i in 0 until dataExp.size) {

            expAmount += dataExp[i].amount
        }
        tvExpense.text = expAmount.toString()

        val balance: Int = incAmount - expAmount
        tvBalance.text = balance.toString()
        /* Getting & Setting Inc, Exp & Balance END */
    }

    private fun pieChartData(startDate: CharSequence, endDate: CharSequence) {

        val dataIncc = db.readIncChart(startDate, endDate)
        visitors.clear()
        for (i in 0 until dataIncc.size) {

            val data: Int = dataIncc[i].amount
            val dataName: String = dataIncc[i].memo
            visitors.add(PieEntry(data.toFloat(), dataName))
        }

        if (dataIncc.isEmpty()) {

            pieChartIncome.visibility = View.GONE
        } else {
            pieChartIncome.visibility = View.VISIBLE
        }

        val dataExpp = db.readExpChart(startDate, endDate)
        visitorsExpense.clear()
        for (i in 0 until dataExpp.size) {

            val dataExpense: Int = dataExpp[i].amount
            val dataNameExpense: String = dataExpp[i].memo
            visitorsExpense.add(PieEntry(dataExpense.toFloat(), dataNameExpense))
        }

        if (dataExpp.isEmpty()) {

            pieChartExpense.visibility = View.GONE
        } else {
            pieChartExpense.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasPermission = ContextCompat.checkSelfPermission(baseContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE) === PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.INTERNET
                ),
                        REQUEST_WRITE_STORAGE)
            } else {
                //startMainActivity()
            }
        }

        title = "Dashboard"
        tvChart = findViewById(R.id.tvChart)
        tvTransactions = findViewById(R.id.tvTransactions)
        tvNoTransactions = findViewById(R.id.tvNoTransactions)
        tvPercentage = findViewById(R.id.tvPercentage)
        tvPercentage.visibility = View.INVISIBLE
        line2 = findViewById(R.id.line2)
        pieChartIncome = findViewById(R.id.pieChartIncome)
        pieChartExpense = findViewById(R.id.pieChartExpense)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        if (!sharedPreferences.getBoolean("ISFIRST", false)) {

            databaseCategorySetup()

            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("ISFIRST", true)
            editor.apply()
        }

        tvIncome = findViewById(R.id.tvIncome)
        tvExpense = findViewById(R.id.tvExpense)
        tvBalance = findViewById(R.id.tvBalance)
        calendarView = findViewById(R.id.calendarView)
        tvMonth = findViewById(R.id.tvMonth)
        calendarView.visibility = View.GONE


        /*Pie chart Initialization START*/
        val pieDataSet = PieDataSet(visitors, "Income")
        pieDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        pieDataSet.valueTextColor = Color.WHITE
        pieDataSet.valueTextSize = 10f
        pieDataSet.setDrawValues(true)

        val pieData = PieData(pieDataSet)

        pieChartIncome.data = pieData
        pieChartIncome.setDrawEntryLabels(false)
        pieChartIncome.centerText = "Income"
        pieChartIncome.description.isEnabled = false
        pieChartIncome.setUsePercentValues(true)
        pieChartIncome.animate()

        val pieDataSetExpense = PieDataSet(visitorsExpense, "Expenses")
        pieDataSetExpense.setColors(*ColorTemplate.COLORFUL_COLORS)
        pieDataSetExpense.valueTextColor = Color.WHITE
        pieDataSetExpense.valueTextSize = 10f
        pieDataSetExpense.setDrawValues(true)

        val pieDataExpense = PieData(pieDataSetExpense)

        pieChartExpense.data = pieDataExpense
        pieChartExpense.setDrawEntryLabels(false)
        pieChartExpense.centerText = "Expenses"
        pieChartExpense.description.isEnabled = false
        pieChartExpense.setUsePercentValues(true)
        pieChartExpense.animate()
        /*Pie chart Initialization START*/

        /*Calender Text, Getting month START*/
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM")
        val formatted = current.format(formatter)
        when (formatted) {
            "1" -> tvMonth.text = getString(R.string.january)
            "2" -> tvMonth.text = getString(R.string.february)
            "3" -> tvMonth.text = getString(R.string.march)
            "4" -> tvMonth.text = getString(R.string.april)
            "5" -> tvMonth.text = getString(R.string.may)
            "6" -> tvMonth.text = getString(R.string.june)
            "7" -> tvMonth.text = getString(R.string.july)
            "8" -> tvMonth.text = getString(R.string.august)
            "9" -> tvMonth.text = getString(R.string.september)
            "10" -> tvMonth.text = getString(R.string.october)
            "11" -> tvMonth.text = getString(R.string.november)
            "12" -> tvMonth.text = getString(R.string.december)
            else -> Log.i("hi", "Month Name")
        }
        /*Calender Text, Getting month END*/
        println("Cal: $formatted")

        //Getting year START
        val currentYear = LocalDateTime.now()
        val formatterYear = DateTimeFormatter.ofPattern("yyyy")
        val formattedYear = currentYear.format(formatterYear)
        //Getting year END

        //Calling current month data START
        monthData(formatted)
        val startDateStringData = "$formattedYear-$formatted-01"
        val endDateStringData = "$formattedYear-$formatted-$endDa"

        Log.i("LOG", startDateStringData)
        Log.i("LOG", endDateStringData)

        listViewData(startDateStringData, endDateStringData)
        incomeExpenseData(startDateStringData, endDateStringData)
        pieChartData(startDateStringData, endDateStringData)
        //Calling current month data END

        /* Calender onClickListener START*/
        calendarView.setOnDateChangeListener(CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->

            //Finding month Start and getting calender count
            val date = dayOfMonth.toString() + "mo" + (month + 1) + "nth" + year
            var mPattern: Pattern = Pattern.compile("mo(.*?)nth")
            var mMatcher: Matcher = mPattern.matcher(date)
            lateinit var month: String
            while (mMatcher.find()) {

                month = mMatcher.group(1)
                Log.i("Month", month)
            }

            when (month) {
                "1" -> tvMonth.text = getString(R.string.january)
                "2" -> tvMonth.text = getString(R.string.february)
                "3" -> tvMonth.text = getString(R.string.march)
                "4" -> tvMonth.text = getString(R.string.april)
                "5" -> tvMonth.text = getString(R.string.may)
                "6" -> tvMonth.text = getString(R.string.june)
                "7" -> tvMonth.text = getString(R.string.july)
                "8" -> tvMonth.text = getString(R.string.august)
                "9" -> tvMonth.text = getString(R.string.september)
                "10" -> tvMonth.text = getString(R.string.october)
                "11" -> tvMonth.text = getString(R.string.november)
                "12" -> tvMonth.text = getString(R.string.december)
                else -> Log.i("hi", "Month Name")
            }

            monthData(month)

            Log.i("Month DAte", endDa)
            //Finding month and getting calender count End

            //Getting year and month start
            val yearMonthString = dayOfMonth.toString() + "mon" + year + "-" + (month + 1) + "th"
            calendarView.visibility = View.GONE
            Log.i("Calender", yearMonthString)

            mPattern = Pattern.compile("mon(.*?)th")
            mMatcher = mPattern.matcher(yearMonthString)

            var yearMonth = ""
            while (mMatcher.find()) {

                yearMonth = mMatcher.group(1)
                Log.i("Cale", yearMonth)
            }

            var startDateString = ""
            for (i in 0 until yearMonth.length - 1) {

                startDateString += yearMonth[i].toString()
            }
            Log.i("CaleDD", startDateString)
            //Getting year and month end

            val startDate: CharSequence = "$startDateString-01"
            val endDate: CharSequence = startDateString + endDa

//            val model: List<User> = db.readData()
            listViewData(startDate, endDate)
            incomeExpenseData(startDate, endDate)
            pieChartData(startDate, endDate)
        })
        /* Calender onClickListener END*/

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

        //Profile data
        try {
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                val personName = acct.displayName
                //val personGivenName = acct.givenName
                //val personFamilyName = acct.familyName
                val personEmail = acct.email
                // val personId = acct.id
                val personPhoto: Uri? = acct.photoUrl

                //Glide.with(this).load(personPhoto).into(ivProfile)

                val headerView = navView.getHeaderView(0)
                tvNameNav = headerView.findViewById(R.id.tvNameNav)
                tvEmailNav = headerView.findViewById(R.id.tvEmailNav)
                ivProfileNav = headerView.findViewById(R.id.ivProfileNav)
                tvNameNav.text = personName
                tvEmailNav.text = personEmail
                Glide.with(this).load(personPhoto).into(ivProfileNav)
            }
        } catch (e: Exception) {
            Log.i("Error", e.printStackTrace().toString())
        }
        //Profile End
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