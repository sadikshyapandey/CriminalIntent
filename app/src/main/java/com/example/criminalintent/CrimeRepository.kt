
package com.example.criminalintent

import android.content.Context
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CrimeRepository(context: Context) {

    private val crimeDao =
        CrimeDatabase.getDatabase(context).crimeDao()

    fun getCrimes(): Flow<List<Crime>> =
        crimeDao.getCrimes()

    suspend fun getCrime(id: UUID): Crime? =
        crimeDao.getCrime(id)

    suspend fun addCrime(crime: Crime) {
        crimeDao.insertCrime(crime)
    }

    suspend fun updateCrime(crime: Crime) {
        crimeDao.updateCrime(crime)
    }

    suspend fun deleteCrime(crime: Crime) {
        crimeDao.deleteCrime(crime)
    }
}
