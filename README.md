# FOGDetection Android App

## 🧠 What is this app?

FOGDetection is an Android app designed to detect fall risk in elderly or vulnerable patients. It uses the phone's **accelerometer** sensor to measure body movement, and if it detects **sudden or dangerous movement**, it alerts the user with a **10-second emergency sound**. It also includes features like **medicine reminders** and **emergency SOS calling**.

---

## 📱 Main Features

1. **Fall Detection using Sensors**:
   - The app checks body movement through the phone's **accelerometer**.
   - If acceleration is **greater than 15 m/s²**, the app assumes fall risk is HIGH.
   - It shows a **red alert screen**, gives a **warning message**, and **plays a 10-second alert sound**.

2. **Emergency Call (SOS)**:
   - One button click to **open the phone dialer** with the saved emergency number.

3. **Medicine Reminder**:
   - User can **add a medicine name, dose, and frequency**.
   - The app stores this info in **SharedPreferences** and shows it on the homepage.
   - When the medicine is taken, the user clicks "Taken" to mark it.

4. **Real-Time Graph**:
   - Shows **live acceleration data** using `GraphView` line charts.

5. **Music Alert Button**:
   - User can manually play the alert sound from the **music button**.

---

## 🤖 Machine Learning Model

This project includes a **TensorFlow Lite (.tflite)** model file.
- The model is stored in the `assets` folder as `model.tflite`.
- It is loaded in the `MainActivity.kt` using the **TensorFlow Lite Interpreter**.
- The input to the model is a 4-feature float array (`[1.0, 1.0, 1.0, 1.0]` used in testing).
- The model gives an output prediction which is shown as a **Toast message**.
- The training of this model 

---

## 📂 Project Structure

```
app/
├── java/com/example/FOGDetection/
│   ├── HomepageActivity.kt      # Main sensor-based UI screen
│   ├── AddMedicationActivity.kt # Add medicine info screen
│   ├── LoginActivity.kt         # Login page
│   ├── SignupActivity.kt        # Signup page
│   ├── ForgetPassword.kt        # Forgot password page
├── res/layout/
│   ├── home.xml                 # Layout for homepage
│   ├── addmedication.xml        # Layout for medication form
│   └── ...                      # Other layouts
├── res/raw/
│   └── emergency.mp3            # 10-second emergency alert sound
├── assets/
│   └── model.tflite             # TensorFlow Lite model
```

---

## 🛠 How the Sound Alert Works

- The alert sound is played when `acceleration > 15` in `onSensorChanged()`.
- It uses `MediaPlayer.create(this, R.raw.emergency)`.
- A `Handler` stops the sound after **10 seconds** using `postDelayed()`.

---

## ✅ How to Fix Common Issues

- If **HomepageActivity** is not found: Make sure all packages use `com.example.FOGDetection`, not `com.example.fdetection`.
- If **sound doesn’t play long enough**: Ensure the audio file is at least 10 seconds, and `Handler` delay is `10000` ms.
- If **model crashes**: Input must match expected shape, e.g., `[1, 4]` float array.

---

## 🧾 Summary

This app is a smart, lightweight tool for detecting fall risks using sensor data, alerting users with sound, managing medication, and providing emergency help. It also integrates a TensorFlow Lite model for prediction tasks.
