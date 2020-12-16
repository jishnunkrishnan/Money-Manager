package com.jishnunkrishnan.moneymanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlin.collections.ArrayList

const val DATABASE_NAME = "MyDB"
const val TABLE_NAME = "Users"
const val COL_ID = "id"
const val COL_INCOMEEXPENSE = "incomeexpense"
const val COL_CATEGORY = "category"
const val COL_MEMO = "memo"
const val COL_AMOUNT = "amount"
const val COL_DATE = "date"

const val CAT_TABLE_NAME = "Category"
const val COL_CAT_ID = "id"
const val COL_INEXCATEGORY = "inexcategory"
const val COL_CAT_NAME = "categoryname"

const val PROFILE_TABLE_NAME = "Profile"
const val COL_PROFILE_ID = "id"
const val COL_PROFILE_NAME = "name"
const val COL_PROFILE_EMAIL = "email"

class DataBaseHandler(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

        val createTable = "CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COL_INCOMEEXPENSE VARCHAR(256),$COL_CATEGORY VARCHAR(256),$COL_MEMO VARCHAR(256),$COL_AMOUNT INTEGER,$COL_DATE DATETIME)"
        val createCategoryTable = "CREATE TABLE $CAT_TABLE_NAME ($COL_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COL_INEXCATEGORY VARCHAR(256),$COL_CAT_NAME VARCHAR(256))"
        val createProfileTable = "CREATE TABLE $PROFILE_TABLE_NAME ($COL_PROFILE_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COL_PROFILE_NAME VARCHAR(256),$COL_PROFILE_EMAIL VARCHAR(256))"

        db?.execSQL(createTable)
        db?.execSQL(createCategoryTable)
        db?.execSQL(createProfileTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
/*
    private fun getDateTime(): String? {
        val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
*/
    fun getDataDate(startDate: CharSequence, endDate: CharSequence): MutableList<User> {

        val dataList: MutableList<User> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COL_DATE > '$startDate' AND $COL_DATE < '$endDate'"

        val result = db.rawQuery(query,null)
        if (result.moveToFirst()) {
            do {
                val user = User()
               // user.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                user.category = result.getString(result.getColumnIndex(COL_CATEGORY))
                user.incomeexpense = result.getString(result.getColumnIndex(COL_INCOMEEXPENSE))
                user.memo = result.getString(result.getColumnIndex(COL_MEMO))
                user.amount = result.getString(result.getColumnIndex(COL_AMOUNT)).toInt()
                user.date = result.getString(result.getColumnIndex(COL_DATE))
                dataList.add(user)
            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return dataList
    }

    fun insertProfile(user: UserProfile) {

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_PROFILE_NAME, user.name)
        cv.put(COL_PROFILE_EMAIL, user.email)
        val result = db.insert(PROFILE_TABLE_NAME, null, cv)

        if (result == (-1).toLong()) {
            Log.i("Error", "Failed")
        } else {
            Log.i("Success", "Success")
        }
    }

    fun updateProfile(user: UserProfile, id: String) {

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_PROFILE_NAME, user.name)
        cv.put(COL_PROFILE_EMAIL, user.email)

        db.update(PROFILE_TABLE_NAME, cv, "$COL_PROFILE_ID=?", arrayOf(id))
        db.close()
    }

    fun readProfile(): MutableList<UserProfile> {

        val list: MutableList<UserProfile> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM $PROFILE_TABLE_NAME"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()) {
            do {

                val userProfile = UserProfile()
                userProfile.name = result.getString(result.getColumnIndex(COL_PROFILE_NAME))
                userProfile.email = result.getString(result.getColumnIndex(COL_PROFILE_EMAIL))
                list.add(userProfile)

            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun insertData(user: User) {

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_INCOMEEXPENSE, user.incomeexpense)
        cv.put(COL_CATEGORY, user.category)
        cv.put(COL_MEMO, user.memo)
        cv.put(COL_AMOUNT, user.amount)
        cv.put(COL_DATE, user.date)
        val result = db.insert(TABLE_NAME, null, cv)

        if (result == (-1).toLong()) {
            Log.i("Error", "Failed")
        } else {
            Log.i("Succes", "Success")
        }
    }

    fun insertCategory(userCategory: UserCategory) {

        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_INEXCATEGORY, userCategory.inexcategory)
        contentValues.put(COL_CAT_NAME, userCategory.categoryname)

        val res = db.insert(CAT_TABLE_NAME, null, contentValues)
        if (res == (-1).toLong()) {
            Log.i("Error", "Failed")
        } else {
            Log.i("Succes", "Success")
        }
    }

    fun readIncomeCategory(): MutableList<UserCategory> {

        val list: MutableList<UserCategory> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM $CAT_TABLE_NAME WHERE $COL_INEXCATEGORY = 'Income'"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()) {
            do {

                val userCategory = UserCategory()
                userCategory.inexcategory = result.getString(result.getColumnIndex(COL_INEXCATEGORY))
                userCategory.categoryname = result.getString(result.getColumnIndex(COL_CAT_NAME))
                list.add(userCategory)

            } while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }

    fun readExpenseCategory(): MutableList<UserCategory> {

        val list: MutableList<UserCategory> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM $CAT_TABLE_NAME WHERE $COL_INEXCATEGORY = 'Expense'"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()) {
            do {

                val userCategory = UserCategory()
                userCategory.inexcategory = result.getString(result.getColumnIndex(COL_INEXCATEGORY))
                userCategory.categoryname = result.getString(result.getColumnIndex(COL_CAT_NAME))
                list.add(userCategory)

            } while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }

    fun readDataExport(): MutableList<UserExport> {

        val list: MutableList<UserExport> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()) {
            do {

                val user = UserExport()
                user.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                user.date = result.getString(result.getColumnIndex(COL_DATE))
                user.incomeexpense = result.getString(result.getColumnIndex(COL_INCOMEEXPENSE))
                user.category = result.getString(result.getColumnIndex(COL_CATEGORY))
                user.memo = result.getString(result.getColumnIndex(COL_MEMO))
                user.amount = result.getString(result.getColumnIndex(COL_AMOUNT)).toInt()
                list.add(user)

            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun readInc(startDate: CharSequence, endDate: CharSequence): MutableList<UserIncExp> {

        val listInfo: ArrayList<UserIncExp> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COL_INCOMEEXPENSE = 'Income' AND $COL_DATE > '$startDate' AND $COL_DATE < '$endDate'"

        //val query = "SELECT * FROM $TABLE_NAME WHERE $COL_DATE > '$startDate' AND $COL_DATE < '$endDate'"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val incomeExpense = result.getString(result.getColumnIndex(COL_INCOMEEXPENSE))
                val amount = result.getInt(result.getColumnIndex(COL_AMOUNT))
                val model = UserIncExp(incomeExpense, amount)
                listInfo.add(model)
            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return listInfo
    }

    fun readExp(startDate: CharSequence, endDate: CharSequence): MutableList<UserIncExp> {

        val listInfo: ArrayList<UserIncExp> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COL_INCOMEEXPENSE = 'Expense' AND $COL_DATE > '$startDate' AND $COL_DATE < '$endDate'"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val incomeExpense = result.getString(result.getColumnIndex(COL_INCOMEEXPENSE))
                val amount = result.getInt(result.getColumnIndex(COL_AMOUNT))
                val model = UserIncExp(incomeExpense, amount)
                listInfo.add(model)
            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return listInfo
    }

    fun readIncChart(startDate: CharSequence, endDate: CharSequence): MutableList<UserChart> {

        val listInfo: ArrayList<UserChart> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COL_INCOMEEXPENSE = 'Income' AND $COL_DATE > '$startDate' AND $COL_DATE < '$endDate'"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val incomeExpense = result.getString(result.getColumnIndex(COL_INCOMEEXPENSE))
                val amount = result.getInt(result.getColumnIndex(COL_AMOUNT))
                val memo = result.getString(result.getColumnIndex((COL_MEMO)))
                val model = UserChart(incomeExpense,amount, memo)
                listInfo.add(model)
            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return listInfo
    }

    fun readExpChart(startDate: CharSequence, endDate: CharSequence): MutableList<UserChart> {

        val listInfo: ArrayList<UserChart> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COL_INCOMEEXPENSE = 'Expense' AND $COL_DATE > '$startDate' AND $COL_DATE < '$endDate'"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val incomeExpense = result.getString(result.getColumnIndex(COL_INCOMEEXPENSE))
                val amount = result.getInt(result.getColumnIndex(COL_AMOUNT))
                val memo = result.getString(result.getColumnIndex((COL_MEMO)))
                val model = UserChart(incomeExpense,amount, memo)
                listInfo.add(model)
            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return listInfo
    }
}