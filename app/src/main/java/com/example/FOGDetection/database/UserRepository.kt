package com.example.fdetection.database



class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User): Boolean {
        val existingUser = userDao.getUserByEmail(user.email)
        return if (existingUser == null) {
            userDao.insertUser(user)
            true // Registration successful
        } else {
            false // Email already exists
        }
    }

    suspend fun upDateUser(user: User): Boolean {
        return try {
            userDao.updateUser(user)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



    suspend fun getAllUser(): List<User> {
       val users : List<User> = userDao.getAllUsers();
        return users;
    }


}
