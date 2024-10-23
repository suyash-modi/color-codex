package com.droid.colorcodex


import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ColorRepository(application: Application) {
    private val colorDao: ColorDao
    private val unsyncedColors: Flow<List<ColorData>>

    init {
        val database = ColorDatabase.getDatabase(application)
        colorDao = database.colorDao()
        unsyncedColors = colorDao.getUnsyncedColorsFlow() // Use Flow for reactive updates
    }

    fun getAllColors(): Flow<List<ColorData>> = colorDao.getAllColors()

    fun insertColor(colorData: ColorData) {
        CoroutineScope(Dispatchers.IO).launch {
            colorDao.insert(colorData)
        }
    }

    fun syncColorsToCloud() {
        CoroutineScope(Dispatchers.IO).launch {
            val colorsToSync = unsyncedColors.first()

            if (colorsToSync.isNotEmpty()) {
               // Log.d("syncColor", "Found unsynced colors: ${colorsToSync.size}")


                val database = Firebase.database.reference

                colorsToSync.forEach { color ->
                 //   Log.d("syncColor", "Attempting to sync color: ${color.colorCode}")


                    database.child("colors").push().setValue(color).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                       //     Log.d("syncColor", "Sync successful: ${color.colorCode}")
                            CoroutineScope(Dispatchers.IO).launch {
                                updateLocalSyncStatus(color)

                            }
                        } else {
                         //   Log.e("syncColor", "Failed to sync color: ${color.colorCode}", task.exception)
                        }
                    }.addOnFailureListener { exception ->
                     //   Log.e("syncColor", "Firebase error occurred: ${exception.message}")
                    }
                }
            } else {
              //  Log.d("syncColor", "No unsynced colors found.")
            }
                }
            }

 //    Function to update local database sync status
    private suspend fun updateLocalSyncStatus(color: ColorData) {
        try {
            colorDao.updateSyncStatus(color.id) // Mark as synced
        //    Log.d("syncColor", "Updated sync status for color: ${color.colorCode}")
        } catch (e: Exception) {
         //   Log.e("syncColor", "Failed to update local sync status for color: ${color.colorCode}", e)
        }
    }

}
