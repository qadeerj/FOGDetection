package com.example.fdetection

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fdetection.HomepageActivity
import com.example.fdetection.R


class AddMedicationActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addmedication)

        // UI Elements
        val backButton: ImageView = findViewById(R.id.backButton)
        val saveButton: Button = findViewById(R.id.saveMedicationButton)
        val medicationNameInput: EditText = findViewById(R.id.medicationName)
        val medicationDoseInput: EditText = findViewById(R.id.medicationDose)
        val medicationFrequencyInput: EditText = findViewById(R.id.medicationFrequency)

        val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Navigate back to Home
        backButton.setOnClickListener {
            startActivity(Intent(this, HomepageActivity::class.java))
            finish()
        }

        // Save Medication Data (Locally or For Future Use)
        saveButton.setOnClickListener {
            val medicationName = medicationNameInput.text.toString().trim()
            val medicationDose = medicationDoseInput.text.toString().trim()
            val medicationFrequency = medicationFrequencyInput.text.toString().trim()

            if (medicationName.isNotEmpty() && medicationDose.isNotEmpty() && medicationFrequency.isNotEmpty()) {
                val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

                val editor = sharedPreferences.edit()
                editor.putBoolean("isMedicin", true)
                editor.putBoolean("isTaken", false)
                editor.putString("mname", medicationName)
                editor.putString("mdose", medicationDose)
                editor.putString("mfrequency", medicationFrequency)
                editor.apply()


                Toast.makeText(this, "Medication Saved!", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, DashBoard::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
