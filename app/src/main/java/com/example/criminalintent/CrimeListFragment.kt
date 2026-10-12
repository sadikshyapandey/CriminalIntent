package com.example.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Date

class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (CrimeRepository.getCrimes().isEmpty()) {
            CrimeRepository.addCrime(
                Crime(title = "Broken office printer", date = Date())
            )
            CrimeRepository.addCrime(
                Crime(title = "Missing office supplies", date = Date())
            )
            CrimeRepository.addCrime(
                Crime(title = "Late report", date = Date())
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_crime_list,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        crimeRecyclerView =
            view.findViewById(R.id.crime_recycler_view)

        crimeRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        crimeRecyclerView.adapter =
            CrimeListAdapter(CrimeRepository.getCrimes()) { crime ->
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        CrimeDetailFragment.newInstance(crime.id)
                    )
                    .addToBackStack(null)
                    .commit()
            }
    }
}