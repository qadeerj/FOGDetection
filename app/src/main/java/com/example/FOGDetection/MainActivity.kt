package com.example.fdetection

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private lateinit var tflite: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)  // XML file must match this

        try {
            tflite = Interpreter(loadModelFile(this, "model.tflite"))

            // Load test image from drawable folder (replace with real input later)
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_image)
            val inputBuffer = preprocessImage(bitmap)

            // Output buffer
            val output = Array(1) { FloatArray(1) }

            // Run model
            tflite.run(inputBuffer, output)

            val result = output[0][0]
            Log.d("TFLite", "Prediction: $result")
            Toast.makeText(this, "Prediction: $result", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Log.e("TFLite", "Error during model inference: ${e.message}")
            Toast.makeText(this, "Model error: ${e.message}", Toast.LENGTH_LONG).show()
        }

        // Buttons
        val loginButton: Button = findViewById(R.id.loginButton)
        val signUpButton: Button = findViewById(R.id.signUpButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    // Load model from assets
    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Convert Bitmap to ByteBuffer for model input
    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val inputSize = 224
        val scaled = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val byteBuffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        scaled.getPixels(intValues, 0, scaled.width, 0, 0, scaled.width, scaled.height)

        for (pixel in intValues) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }
        return byteBuffer
    }
}
