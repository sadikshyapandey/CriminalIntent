package com.example.criminalintent

import java.util.UUID

object CrimeRepository {

    private val crimes = mutableListOf<Crime>()

    @Synchronized
    fun getCrimes(): List<Crime> {
        return crimes.toList()
    }

    @Synchronized
    fun getCrime(id: UUID): Crime? {
        return crimes.find { it.id == id }
    }

    @Synchronized
    fun addCrime(crime: Crime) {
        if (crimes.none { it.id == crime.id }) {
            crimes.add(crime)
        }
    }

    @Synchronized
    fun updateCrime(crime: Crime) {
        val index = crimes.indexOfFirst { it.id == crime.id }
        if (index >= 0) {
            crimes[index] = crime
        }
    }

    @Synchronized
    fun deleteCrime(id: UUID) {
        crimes.removeAll { it.id == id }
    }
}