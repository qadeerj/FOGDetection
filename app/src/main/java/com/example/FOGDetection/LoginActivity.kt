package com.example.fdetection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fdetection.database.AppDatabase
import com.example.fdetection.database.User
import com.example.fdetection.database.UserDao
import com.example.fdetection.database.UserRepository
import com.example.fdetection.database.UserViewModel


class LoginActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    companion object {
        var user: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)  // Ensure your XML is named `login.xml`

        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val loginButton: Button = findViewById(R.id.loginButton)
        val forgotPassword: TextView = findViewById(R.id.forgotPassword)

        val database = AppDatabase.getDatabase(this)
        val userDao = database.userDao()
        val repository = UserRepository(userDao)
        userViewModel = UserViewModel(repository)
        // Login Button Click
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {

                var isExist: Boolean = false;

                userViewModel.getAllUsers { users ->
                    if (users.isNotEmpty()) {
                        for (user in users) {
                            Log.d("emaildkjfl","email: "+user.email +" current: "+email);
                            Log.d("emaildkjfl","pass: "+user.password +" current: "+password);
                            if (user.email == email && user.password == password)
                            {
                               isExist = true;
                                LoginActivity.user = user;
                                break
                            }

                        }

                        if(isExist)
                        {
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                            // Navigate to the next screen
                            val intent = Intent(this, DashBoard::class.java)
                            startActivity(intent)
                            finish()
                        }else
                        {
                            Toast.makeText(this, "Invalid Email or Password!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }

        // Forgot Password Click
        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgetPassword::class.java)
            startActivity(intent)
          //  finish()
        }
    }
}
