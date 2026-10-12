
package com.example.criminalintent

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date
import java.util.UUID

class CrimeTypeConverters {

    @TypeConverter
    fun fromDate(date: Date): Long = date.time

    @TypeConverter
    fun toDate(value: Long): Date = Date(value)

    @TypeConverter
    fun fromUUID(uuid: UUID): String = uuid.toString()

    @TypeConverter
    fun toUUID(value: String): UUID = UUID.fromString(value)
}

@Database(
    entities = [Crime::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {

    abstract fun crimeDao(): CrimeDao

    companion object {
        @Volatile
        private var INSTANCE: CrimeDatabase? = null

        fun getDatabase(context: Context): CrimeDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CrimeDatabase::class.java,
                    "crime_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
