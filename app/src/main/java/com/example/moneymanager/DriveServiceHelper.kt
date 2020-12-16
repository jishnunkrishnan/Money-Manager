package com.example.moneymanager

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DriveServiceHelper(googleDriveService: Drive) {

    private val mExecutor: Executor = Executors.newSingleThreadExecutor()
    private var googleDriveService: Drive? = googleDriveService

//    fun DriveServiceHelper(googleDriveService: Drive?) {
//        this.mDriveService = googleDriveService
//    }

    fun createFilePDF(filePath: String?): Task<String>? {
        return Tasks.call(mExecutor, {
            val fileMetaData = File()
            fileMetaData.name = "MyExcelFile"

            fileMetaData.mimeType = "application/vnd.ms-excel"

            val file = java.io.File(filePath)
//            val mediaContent = FileContent("application/pdf", file)
            val mediaContent = FileContent("text/csv", file)
            var myFile: File? = null
            try {
                myFile = googleDriveService!!.files().create(fileMetaData,mediaContent).setFields("id").execute()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("eee", e.toString())
            }
            if (myFile == null) {
                throw IOException("Null result when requesting file creation")
            }
            myFile.id
        })
    }
}