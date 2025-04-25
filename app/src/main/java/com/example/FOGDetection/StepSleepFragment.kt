package com.example.fdetection

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class StepSleepFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepsToday = 0

    private lateinit var tvSteps: TextView
    private lateinit var tvSleep: TextView
    private lateinit var etSleep: EditText
    private lateinit var btnSaveSleep: Button
    private lateinit var prefs: SharedPreferences

    private val PERMISSION_ACTIVITY_RECOGNITION = 1001

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_step_sleep, container, false)

        tvSteps = view.findViewById(R.id.textViewSteps)
        tvSleep = view.findViewById(R.id.textViewSleep)
        etSleep = view.findViewById(R.id.editTextSleep)
        btnSaveSleep = view.findViewById(R.id.buttonSaveSleep)

        prefs = requireContext().getSharedPreferences("step_prefs", Context.MODE_PRIVATE)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(requireContext(), "Step sensor not available", Toast.LENGTH_LONG).show()
        }

        // Ask for permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                PERMISSION_ACTIVITY_RECOGNITION
            )
        }

        btnSaveSleep.setOnClickListener {
            val hours = etSleep.text.toString()
            if (hours.isNotEmpty()) {
                prefs.edit().putString("sleep_${today()}", hours).apply()
                tvSleep.text = "Sleep: $hours hrs"
                Toast.makeText(requireContext(), "Sleep saved", Toast.LENGTH_SHORT).show()
            }
        }

        // Load previously saved sleep data
        val savedSleep = prefs.getString("sleep_${today()}", null)
        if (savedSleep != null) {
            tvSleep.text = "Sleep: $savedSleep hrs"
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        stepSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()

            val savedDate = prefs.getString("date", "")
            val savedInitial = prefs.getInt("initial_steps", -1)

            if (savedInitial == -1 || savedDate != today()) {
                prefs.edit()
                    .putString("date", today())
                    .putInt("initial_steps", totalSteps)
                    .apply()
                stepsToday = 0
            } else {
                stepsToday = totalSteps - savedInitial
            }

            tvSteps.text = "Today's Steps: $stepsToday"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun today(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
