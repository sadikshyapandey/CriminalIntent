
package com.example.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.util.Date

class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var repository: CrimeRepository
    private var hasCheckedInitialCrimes = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = CrimeRepository(requireContext())
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        crimeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val addButton: Button = view.findViewById(R.id.add_crime_button)

        addButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val newCrime = Crime(title = "New Crime", date = Date())
                repository.addCrime(newCrime)

                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        CrimeDetailFragment.newInstance(newCrime.id)
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.getCrimes().collect { crimes ->

                    if (!hasCheckedInitialCrimes) {
                        hasCheckedInitialCrimes = true

                        if (crimes.isEmpty()) {
                            repository.addCrime(
                                Crime(
                                    title = "Broken office printer",
                                    date = Date()
                                )
                            )
                            repository.addCrime(
                                Crime(
                                    title = "Missing office supplies",
                                    date = Date()
                                )
                            )
                            repository.addCrime(
                                Crime(
                                    title = "Late report",
                                    date = Date()
                                )
                            )
                            return@collect
                        }
                    }

                    crimeRecyclerView.adapter =
                        CrimeListAdapter(crimes) { crime ->
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
        }
    }
}
