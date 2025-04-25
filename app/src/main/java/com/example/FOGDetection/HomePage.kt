package com.example.fdetection

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.SimpleDateFormat
import java.util.*

class HomepageActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var graphView: GraphView
    private lateinit var fallRiskStatus: TextView
    private lateinit var fallRiskMessage: TextView
    private lateinit var medicationName: TextView
    private lateinit var medicationDose: TextView
    private lateinit var medicationTakenButton: Button
    private lateinit var addMedicationButton: ImageButton
    private lateinit var sosButton: ImageButton
    private lateinit var musicButton: ImageButton
    private lateinit var dateText: TextView
    private lateinit var usernameText: TextView

    private var mediaPlayer: MediaPlayer? = null
    private val dataSeries = LineGraphSeries<DataPoint>()
    private var lastXValue = 0.0
    private var isPlay: Boolean = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        sosButton = findViewById(R.id.sosButton)
        val avatarImageView: ImageView = findViewById(R.id.avatarImageView)
        val helloLabel: TextView = findViewById(R.id.helloLabel)
        usernameText = findViewById(R.id.usernameText)
        addMedicationButton = findViewById(R.id.addMedicationButton)
        dateText = findViewById(R.id.dateText)

        fallRiskStatus = findViewById(R.id.fallRiskStatus)
        fallRiskMessage = findViewById(R.id.fallRiskMessage)
        medicationTakenButton = findViewById(R.id.takenButton)
        medicationName = findViewById(R.id.medicationName)
        medicationDose = findViewById(R.id.medicationDose)
        graphView = findViewById(R.id.graphView)
        musicButton = findViewById(R.id.musicButton)

        val homeButton: LinearLayout = findViewById(R.id.homeButton)
        val activityButton: LinearLayout = findViewById(R.id.activityButton)
        val watchButton: LinearLayout = findViewById(R.id.watchButton)

        setCurrentDate()
        loadUsername()
        setupGraph()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        sosButton.setOnClickListener {
            callEmergency()
        }

        val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val mname = sharedPreferences.getString("mname", "N/A")
        val mdose = sharedPreferences.getString("mdose", "N/A")
        val mfrequency = sharedPreferences.getString("mfrequency", "")
        val isTaken = sharedPreferences.getBoolean("isTaken", false)

        val editor = sharedPreferences.edit()
        medicationName.text = mname
        medicationDose.text = "$mdose-$mfrequency"

        if (isTaken) {
            medicationTakenButton.text = "✔ Taken"
            medicationTakenButton.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
            medicationTakenButton.setTextColor(ContextCompat.getColor(this, R.color.white))
            medicationTakenButton.isEnabled = false
        }

        medicationTakenButton.setOnClickListener {
            medicationTakenButton.text = "✔ Taken"
            medicationTakenButton.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
            medicationTakenButton.setTextColor(ContextCompat.getColor(this, R.color.white))
            medicationTakenButton.isEnabled = false
            editor.putBoolean("isTaken", true)
            editor.apply()
        }

        addMedicationButton.setOnClickListener {
            val intent = Intent(this, AddMedicationActivity::class.java)
            startActivity(intent)
        }

        musicButton.setOnClickListener {
            playAlertSound()
        }

        activityButton.setOnClickListener { finish() }
        watchButton.setOnClickListener { finish() }
        updateSelectedButton(homeButton)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { acc ->
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val acceleration = Math.sqrt(
                (event.values[0] * event.values[0] +
                        event.values[1] * event.values[1] +
                        event.values[2] * event.values[2]).toDouble()
            )

            updateGraph(acceleration)
            updateFallRisk(acceleration)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun setupGraph() {
        graphView.addSeries(dataSeries)
        graphView.viewport.isScrollable = true
        graphView.viewport.isScalable = true
        graphView.viewport.setMinX(0.0)
        graphView.viewport.setMaxX(50.0)
        graphView.viewport.isXAxisBoundsManual = true
        graphView.gridLabelRenderer.verticalAxisTitle = "Acceleration (m/s²)"
    }

    private fun updateGraph(value: Double) {
        lastXValue += 1
        dataSeries.appendData(DataPoint(lastXValue, value), true, 50)
    }

    private fun updateFallRisk(acceleration: Double) {
        val fallRiskContainer: LinearLayout = findViewById(R.id.fallRiskContainer)

        if (acceleration > 15) {
            if (!isPlay) {
                playAlertSound()
                isPlay = true
            }

            fallRiskStatus.text = "HIGH"
            fallRiskStatus.setTextColor(Color.RED)
            fallRiskContainer.setBackgroundResource(R.drawable.red_pink_background)
            fallRiskMessage.text = "Sit Down Immediately!"
            fallRiskMessage.setTextColor(Color.BLACK)
            musicButton.visibility = ImageButton.VISIBLE
        } else {
            fallRiskStatus.text = "LOW"
            fallRiskStatus.setTextColor(Color.GREEN)
            fallRiskContainer.setBackgroundResource(R.drawable.green_background)
            fallRiskMessage.text = "No risks currently,\nYou can move safely."
            fallRiskMessage.setTextColor(Color.BLACK)
            musicButton.visibility = ImageButton.INVISIBLE
        }
    }

    private fun playAlertSound() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, R.raw.emergency)
        mediaPlayer?.start()

        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                    it.release()
                    isPlay = false
                }
            }
        }, 90000)
    }

    private fun callEmergency() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${LoginActivity.user?.emergencyPhone}")
        startActivity(intent)
    }

    private fun setCurrentDate() {
        val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        dateText.text = currentDate
    }

    private fun loadUsername() {
        usernameText.text = LoginActivity.user?.fullName
    }

    private fun updateSelectedButton(selectedButton: LinearLayout) {
        val homeButton: LinearLayout = findViewById(R.id.homeButton)
        val activityButton: LinearLayout = findViewById(R.id.activityButton)
        val watchButton: LinearLayout = findViewById(R.id.watchButton)

        homeButton.setBackgroundResource(android.R.color.transparent)
        activityButton.setBackgroundResource(android.R.color.transparent)
        watchButton.setBackgroundResource(android.R.color.transparent)

        selectedButton.setBackgroundResource(R.drawable.selected_nav_background)
    }
}
