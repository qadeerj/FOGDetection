package com.example.fdetection

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fdetection.SignupActivity
import com.example.fdetection.database.AppDatabase
import com.example.fdetection.database.User
import com.example.fdetection.database.UserRepository
import com.example.fdetection.database.UserViewModel

class ForgetPassword : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forget_password)

        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val updateButton: Button = findViewById(R.id.resetPasswordButton);
        val backToLogin: TextView = findViewById(R.id.backToLogin);

        val database = AppDatabase.getDatabase(this)
        val userDao = database.userDao()
        val repository = UserRepository(userDao)
        userViewModel = UserViewModel(repository)

        updateButton.setOnClickListener {

            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                var isExist: Boolean = false;

                userViewModel.getAllUsers { users ->
                    if (users.isNotEmpty()) {
                        for (user in users) {
                            if(user.email.equals(email))
                            {
                                val newUser = User(
                                    id = user.id,
                                    fullName = user.fullName,
                                    email = user.email,
                                    password = password,
                                    phoneNumber = user.phoneNumber,
                                    contactName = user.contactName,
                                    emergencyPhone = user.emergencyPhone
                                )
                                isExist = true;
                                userViewModel.updateUser(newUser) { isSuccess ->
                                    runOnUiThread {
                                        if (isSuccess) {
                                            Toast.makeText(this@ForgetPassword, "Password Updated!", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this, LoginActivity::class.java))
                                            finish()
                                        } else {
                                            Toast.makeText(this@ForgetPassword, "Update Failed!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                break
                            }

                        }

                        if(!isExist)
                        {
                            Toast.makeText(this, "User not exist!", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }
        }

        backToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}