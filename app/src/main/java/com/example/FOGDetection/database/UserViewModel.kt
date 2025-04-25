package com.example.fdetection.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun registerUser(user: User, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val isSuccess = userRepository.registerUser(user)

            withContext(Dispatchers.Main) {
                callback(isSuccess)
            }
        }


    }

    fun updateUser(user: User, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val isSuccess = userRepository.upDateUser(user)

            withContext(Dispatchers.Main) {
                callback(isSuccess)
            }
        }


    }

    fun getAllUsers( callback: (List<User>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {

            val users: List<User> = userRepository.getAllUser();

            withContext(Dispatchers.Main) {
                callback(users)
            }
        }

    }

}
