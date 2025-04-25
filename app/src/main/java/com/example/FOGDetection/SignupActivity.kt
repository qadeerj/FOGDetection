package com.example.fdetection

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.fdetection.database.AppDatabase
import com.example.fdetection.database.User
import com.example.fdetection.database.UserRepository
import com.example.fdetection.database.UserViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class SignupActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup) // Ensure this matches your XML file

        val fullName: EditText = findViewById(R.id.fullNameInput)
        val email: EditText = findViewById(R.id.emailInput)
        val password: EditText = findViewById(R.id.passwordInput)
        val phone: EditText = findViewById(R.id.phoneNumberInput)
        val contactName: EditText = findViewById(R.id.contactNameInput)
        val contactPhone: EditText = findViewById(R.id.emergencyPhoneInput)
        val termsCheckBox: CheckBox = findViewById(R.id.termsCheckBox)
        val signUpButton: Button = findViewById(R.id.signUpButton)
        val backToLogin: TextView = findViewById(R.id.backToLogin)

        val database = AppDatabase.getDatabase(this)
        val userDao = database.userDao()
        val repository = UserRepository(userDao)
        userViewModel = UserViewModel(repository)  // Initialize only once

        signUpButton.setOnClickListener {
            val name = fullName.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val password = password.text.toString().trim()  // FIX: Corrected password retrieval
            val userPhone = phone.text.toString().trim()
            val emergencyName = contactName.text.toString().trim()
            val emergencyPhone = contactPhone.text.toString().trim()

            if (name.isEmpty() || userEmail.isEmpty() || userPhone.isEmpty() || emergencyName.isEmpty() || emergencyPhone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!termsCheckBox.isChecked) {
                Toast.makeText(this, "You must agree to the terms and conditions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newUser = User(
                fullName = name,
                email = userEmail,
                password = password,  // FIX: Ensure password is retrieved correctly
                phoneNumber = userPhone,
                contactName = emergencyName,
                emergencyPhone = emergencyPhone
            )

            userViewModel.registerUser(newUser) { isSuccess ->
                runOnUiThread {
                    if (isSuccess) {
                        Toast.makeText(this@SignupActivity, "Registration Successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@SignupActivity, "Email already registered!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Navigate back to Login Page
        backToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
