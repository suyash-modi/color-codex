package com.droid.colorcodex


import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "color_table")
data class ColorData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val colorCode: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)

@Dao
interface ColorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(colorData: ColorData)

    @Query("SELECT * FROM color_table")
    fun getAllColors(): Flow<List<ColorData>>

    @Query("SELECT * FROM color_table WHERE isSynced = 0")
    fun getUnsyncedColorsFlow(): Flow<List<ColorData>>

    @Query("UPDATE color_table SET isSynced = 1 WHERE id = :colorId")
    suspend fun updateSyncStatus(colorId: Int): Int
}

@Database(entities = [ColorData::class], version = 1)
abstract class ColorDatabase : RoomDatabase() {
    abstract fun colorDao(): ColorDao

    companion object {
        @Volatile
        private var INSTANCE: ColorDatabase? = null

        fun getDatabase(context: Context): ColorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ColorDatabase::class.java,
                    "color_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
