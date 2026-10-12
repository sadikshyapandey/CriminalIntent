package com.example.criminalintent

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import java.text.DateFormat
import java.util.Calendar
import java.util.UUID

class CrimeDetailFragment : Fragment() {

    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var deleteButton: Button

    private var crime: Crime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crimeId = arguments?.getString(ARG_CRIME_ID)

        crime = crimeId?.let {
            CrimeRepository.getCrime(UUID.fromString(it))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.fragment_crime_detail,
            container,
            false
        )

        titleField = view.findViewById(R.id.crime_title)
        dateButton = view.findViewById(R.id.crime_date)
        solvedCheckBox = view.findViewById(R.id.crime_solved)
        deleteButton = view.findViewById(R.id.crime_delete_button)

        crime?.let { currentCrime ->
            titleField.setText(currentCrime.title)
            dateButton.text =
                DateFormat.getDateInstance().format(currentCrime.date)
            solvedCheckBox.isChecked = currentCrime.isSolved
        }

        dateButton.setOnClickListener {
            showDatePicker()
        }

        deleteButton.setOnClickListener {
            crime?.let { currentCrime ->
                CrimeRepository.deleteCrime(currentCrime.id)
            }
            parentFragmentManager.popBackStack()
        }

        return view
    }

    override fun onPause() {
        super.onPause()

        crime?.let { currentCrime ->
            currentCrime.title = titleField.text.toString()
            currentCrime.isSolved = solvedCheckBox.isChecked
            CrimeRepository.updateCrime(currentCrime)
        }
    }

    private fun showDatePicker() {
        val currentCrime = crime ?: return

        val calendar = Calendar.getInstance().apply {
            time = currentCrime.date
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                currentCrime.date = calendar.time

                dateButton.text =
                    DateFormat.getDateInstance().format(currentCrime.date)

                CrimeRepository.updateCrime(currentCrime)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    companion object {
        private const val ARG_CRIME_ID = "crime_id"

        fun newInstance(crimeId: UUID): CrimeDetailFragment {
            return CrimeDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CRIME_ID, crimeId.toString())
                }
            }
        }
    }
}