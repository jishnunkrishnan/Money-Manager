package com.jishnunkrishnan.moneymanager

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.material.navigation.NavigationView
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.*
import java.io.*

@Suppress("DEPRECATION")
class ExportActivity : AppCompatActivity() {

    private var db = DataBaseHandler(this)
    private var driveServiceHelper: DriveServiceHelper? = null
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var tvNameNav: TextView
    private lateinit var tvEmailNav: TextView
    private lateinit var listViewExport: ListView
    private lateinit var tvReports: TextView
    private lateinit var tvSuccess: TextView
    private lateinit var ivProfileNav: ImageView

    var selectedFile = null
    fun gotoProfile(view: View) {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    fun saveImportDrive(view: View) {


    }

    /* Import from storage excel start*/
    private var TAG = "main"
    private var textView: TextView? = null

    fun callIntent() {
        val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.flags = FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            400 -> if (resultCode == RESULT_OK) {
                if (data != null) {
                    handleSignInIntent(data)
                }
            }
            1 -> if (resultCode == RESULT_OK) {
                val documentPath = data!!.data
                var path = documentPath!!.path.toString()
                val length = path.length
                path = path.substring(6, length)
                val root = Environment.getExternalStorageState()
                val myInput = FileInputStream(path)
                readExcelFileFromAssets(myInput)
            }
        }
    }

    fun getPath(view: View) {
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "*/*"
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        startActivityForResult(chooseFile, 1)
    }

    private fun readExcelFileFromAssets(myInput: InputStream) {
        try {
            // Create a POI File System object
            val myFileSystem = POIFSFileSystem(myInput)
            // Create a workbook using the File System
            val myWorkBook = HSSFWorkbook(myFileSystem)
            // Get the first sheet from workbook
            val mySheet = myWorkBook.getSheetAt(0)
            // We now need something to iterate through the cells.
            val rowIter = mySheet.rowIterator()
            var rowno = 0
            textView!!.append("\n")
            while (rowIter.hasNext()) {
                val myRow = rowIter.next() as HSSFRow
                if (rowno != 0) {
                    val cellIter = myRow.cellIterator()
                    var colno = 0
                    var readDate = ""
                    var readType = ""
                    var readCategory = ""
                    var readMemo = ""
                    var readAmount = ""
                    while (cellIter.hasNext()) {
                        val myCell = cellIter.next() as HSSFCell
                        when (colno) {
                            0 -> {
                                //readSlno = myCell.toString()
                            }
                            1 -> {
                                readDate = myCell.toString()
                            }
                            2 -> {
                                readType = myCell.toString()
                            }
                            3 -> {
                                readCategory = myCell.toString()
                            }
                            4 -> {
                                readMemo = myCell.toString()
                            }
                            5 -> {
                                readAmount = myCell.toString()
                            }
                        }
                        colno++
                        //Log.e(TAG, "Index :" + myCell.toString())
                    }
                   // textView.append("$sno -- $date  -- $det\n")
                   // Log.i("HEILLO Slno", readSlno)

                    val user = User(readType, readCategory, readMemo, readAmount.toInt(), readDate)
                    val db = DataBaseHandler(this)
                    db.insertData(user)
                }
                rowno++
                Toast.makeText(this, getString(R.string.import_success), Toast.LENGTH_LONG).show()
            }
        } catch (e: java.lang.Exception) {
        }
    }
    /* Import from storage excel end*/

    private fun reportFunction() {

        /*List View Transactions START*/
        val model: List<UserExport> = db.readDataExport()

        val empArraySlno = Array(model.size) { "null" }
        val empArrayDate = Array(model.size) { "null" }
        val empArrayType = Array(model.size) { "null" }
        val empArrayCategory = Array(model.size) { "null" }
        val empArrayMemo = Array(model.size) { "null" }
        val empArrayAmount = Array(model.size) { "null" }

        for ((index, i) in model.withIndex()) {

            empArraySlno[index] = i.id.toString()
            empArrayDate[index] = i.date
            empArrayType[index] = i.incomeexpense
            empArrayCategory[index] = i.category
            empArrayMemo[index] = i.memo
            empArrayAmount[index] = i.amount.toString()
        }
        val arrayAdapter = TableAdapter(this, empArraySlno, empArrayDate, empArrayType, empArrayCategory, empArrayMemo, empArrayAmount)
        listViewExport.adapter = arrayAdapter
        /*List View Transactions END*/
    }

    fun saveExport(view: View) {

        saveExcelFile(this, "myExcel.xls")
        reportFunction()
        tvReports.visibility = View.VISIBLE
        tvSuccess.text = getString(R.string.download_success)
    }

    fun saveExportDrive(view: View) {

        val progressDialog = ProgressDialog(this@ExportActivity)
        progressDialog.setTitle(getString(R.string.upload_to_drive))
        progressDialog.setMessage(getString(R.string.please_wait))
        progressDialog.show()

//        val filePath = "/storage/emulated/0/myExcel.xls"
        val filePath = "/storage/emulated/0/Android/data/com.example.moneymanager/files/myExcel.xls"
        driveServiceHelper!!.createFilePDF(filePath)!!.addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(this@ExportActivity, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
            reportFunction()
            tvReports.visibility = View.VISIBLE
            tvSuccess.text = getString(R.string.upload_success_to_drive)
        }.addOnFailureListener { e ->
            progressDialog.dismiss()
            Toast.makeText(this@ExportActivity, getString(R.string.check_internet), Toast.LENGTH_SHORT).show()
        }
    }

    /* Drive sign in code start */
    private fun requestSignIn() {

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE)).build()

        val client = GoogleSignIn.getClient(this, signInOptions)
        startActivityForResult(client.signInIntent, 400)
    }
    /* Drive sign in code end */

    private fun handleSignInIntent(data: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener { googleSignInAccount ->
                    val credential = GoogleAccountCredential
                            .usingOAuth2(this@ExportActivity, setOf(DriveScopes.DRIVE_FILE))
                    credential.selectedAccount = googleSignInAccount.account
                    val googleDriveService = Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            GsonFactory(),
                            credential
                    )
                            .setApplicationName(getString(R.string.app_name))
                            .build()
                    driveServiceHelper = DriveServiceHelper(googleDriveService)
                }
                .addOnFailureListener { }
    }

    /*Getting storage status start*/
    private fun isExternalStorageReadOnly(): Boolean {

        val extStorageState = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState) {
            return true
        }
        return false
    }

    private fun isExternalStorageAvailable(): Boolean {
        val extStorageState = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == extStorageState) {
            return true
        }
        return false
    }
    /*Getting storage status end*/

    /*Creating excel and save to storage start*/
    private fun saveExcelFile(context: Context, fileName: String): Boolean {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e("Hello", "Storage not available or read only")
            return false
        }
        var success = false

        //New Workbook
        val wb: Workbook = HSSFWorkbook()
        var c: Cell?

        //Cell style for header row
        val cs: CellStyle = wb.createCellStyle()
        cs.fillForegroundColor = HSSFColor.LIME.index
        cs.fillPattern = HSSFCellStyle.SOLID_FOREGROUND

        //New Sheet
        val sheet1: Sheet?
        sheet1 = wb.createSheet("myOrder")

        // Generate column headings
        val row: Row = sheet1.createRow(0)

        c = row.createCell(0)
        c.setCellValue(getString(R.string.sl_no))
        c.cellStyle = cs

        c = row.createCell(1)
        c.setCellValue(getString(R.string.date))
        c.cellStyle = cs

        c = row.createCell(2)
        c.setCellValue(getString(R.string.income_expense))
        c.cellStyle = cs

        c = row.createCell(3)
        c.setCellValue(getString(R.string.category))
        c.cellStyle = cs

        c = row.createCell(4)
        c.setCellValue(getString(R.string.memo))
        c.cellStyle = cs

        c = row.createCell(5)
        c.setCellValue(getString(R.string.amount))
        c.cellStyle = cs

        sheet1.setColumnWidth(0, 15 * 500)
        sheet1.setColumnWidth(1, 15 * 500)
        sheet1.setColumnWidth(2, 15 * 500)
        sheet1.setColumnWidth(3, 15 * 500)
        sheet1.setColumnWidth(4, 15 * 500)
        sheet1.setColumnWidth(5, 15 * 500)

        /*List View Transactions START*/
        val model: List<UserExport> = db.readDataExport()
        val empArrayId = Array(model.size) { "null" }
        val empArrayDate = Array(model.size) { "null" }
        val empArrayType = Array(model.size) { "null" }
        val empArrayCategory = Array(model.size) { "null" }
        val empArrayMemo = Array(model.size) { "null" }
        val empArrayAmount = Array(model.size) { "null" }

        for ((index, i) in model.withIndex()) {
            empArrayId[index] = i.id.toString()
            empArrayDate[index] = i.date
            empArrayType[index] = i.incomeexpense
            empArrayCategory[index] = i.category
            empArrayMemo[index] = i.memo
            empArrayAmount[index] = i.amount.toString()
            Log.i("hello index", index.toString())
            Log.i("hello i", i.toString())

            //var roww :String = "row"
            //var con : String = roww + (index + 1).toString()
            for (i in 0..5) {
                val row1: Row = sheet1.createRow(index + 1)

                for (i in 0..5) {
                    when (i) {
                        0 -> {
                            c = row1.createCell(i)
                            c.setCellValue(empArrayId[index])
                        }
                        1 -> {
                            c = row1.createCell(i)
                            c.setCellValue(empArrayDate[index])
                        }
                        2 -> {
                            c = row1.createCell(i)
                            c.setCellValue(empArrayType[index])
                        }
                        3 -> {
                            c = row1.createCell(i)
                            c.setCellValue(empArrayCategory[index])
                        }
                        4 -> {
                            c = row1.createCell(i)
                            c.setCellValue(empArrayMemo[index])
                        }
                        5 -> {
                            c = row1.createCell(i)
                            c.setCellValue(empArrayAmount[index])
                        }
                    }
                }
            }
        }
        /*List View Transactions END*/

        sheet1.setColumnWidth(0, 15 * 500)
        sheet1.setColumnWidth(1, 15 * 500)
        sheet1.setColumnWidth(2, 15 * 500)
        sheet1.setColumnWidth(3, 15 * 500)
        sheet1.setColumnWidth(4, 15 * 500)
        sheet1.setColumnWidth(5, 15 * 500)

        // Create a path where we will place our List of objects on external storage
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        Log.d("hello", file.toString())
//                File(context.getExternalFilesDir(null), fileName)

        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(file)
            wb.write(os)
            Log.w("FileUtils", "Files Stored in: $file")
            Toast.makeText(context, "Files Stored in: $file", Toast.LENGTH_LONG).show()
            success = true
        } catch (e: IOException) {
            Log.w("FileUtils", "Error writing $file", e)
        } catch (e: Exception) {
            Log.w("FileUtils", "Failed to save file", e)
        } finally {
            try {
                os?.close()
            } catch (ex: Exception) {
                Log.e("hello", ex.toString())
            }
        }
        return success
    }
    /*Creating excel and save to storage end*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export)

        textView = findViewById(R.id.textView)
        listViewExport = findViewById(R.id.listViewExport)
        tvSuccess = findViewById(R.id.tvSuccess)
        tvReports = findViewById(R.id.tvReports)

        //Drive Code
        requestSignIn()

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