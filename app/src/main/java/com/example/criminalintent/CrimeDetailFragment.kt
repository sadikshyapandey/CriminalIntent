
package com.example.criminalintent

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.DateFormat
import java.util.Calendar
import java.util.UUID

class CrimeDetailFragment : Fragment() {

    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var deleteButton: Button
    private lateinit var photoButton: Button
    private lateinit var photoImageView: ImageView

    private lateinit var repository: CrimeRepository
    private var crime: Crime? = null
    private var photoUri: Uri? = null

    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                photoUri?.let { uri ->
                    photoImageView.setImageURI(uri)
                }
            }
        }

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
            R.layout.fragment_crime_detail,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleField = view.findViewById(R.id.crime_title)
        dateButton = view.findViewById(R.id.crime_date)
        solvedCheckBox = view.findViewById(R.id.crime_solved)
        deleteButton = view.findViewById(R.id.crime_delete_button)
        photoButton = view.findViewById(R.id.crime_take_photo)
        photoImageView = view.findViewById(R.id.crime_photo)

        val crimeId = arguments?.getString(ARG_CRIME_ID) ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val loadedCrime = repository.getCrime(UUID.fromString(crimeId))
            crime = loadedCrime

            loadedCrime?.let { currentCrime ->
                titleField.setText(currentCrime.title)
                dateButton.text =
                    DateFormat.getDateInstance().format(currentCrime.date)
                solvedCheckBox.isChecked = currentCrime.isSolved
            }
        }

        dateButton.setOnClickListener {
            showDatePicker()
        }

        deleteButton.setOnClickListener {
            val currentCrime = crime ?: return@setOnClickListener

            viewLifecycleOwner.lifecycleScope.launch {
                repository.deleteCrime(currentCrime)
                parentFragmentManager.popBackStack()
            }
        }

        photoButton.setOnClickListener {
            val photoFile = File(
                requireContext().cacheDir,
                "crime_photo_$crimeId.jpg"
            )

            photoUri = Uri.fromFile(photoFile)

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }

            try {
                takePhoto.launch(cameraIntent)
            } catch (_: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Could not open the camera.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onPause() {
        if (::titleField.isInitialized && ::solvedCheckBox.isInitialized) {
            crime?.let { currentCrime ->
                currentCrime.title = titleField.text.toString()
                currentCrime.isSolved = solvedCheckBox.isChecked

                viewLifecycleOwner.lifecycleScope.launch {
                    repository.updateCrime(currentCrime)
                }
            }
        }

        super.onPause()
    }

    private fun showDatePicker() {
        val currentCrime = crime ?: return

        val calendar = Calendar.getInstance().apply {
            time = currentCrime.date
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                currentCrime.date = calendar.time

                dateButton.text =
                    DateFormat.getDateInstance().format(currentCrime.date)

                viewLifecycleOwner.lifecycleScope.launch {
                    repository.updateCrime(currentCrime)
                }
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
