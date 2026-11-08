//package com.example.atulyamvideocreatorapp
//
//import com.example.atulyamvideocreatorapp.speedcontroller.VideoSpeedPreset
//
//class TimerModel(initialTimeMs: Long = 0L) {
//
//    // The core data type remains Long for high-precision timer operations
//    var timeInMs: Long = initialTimeMs
//        private set
//
//    // Option A: Set directly using a Long value
//    fun setTime(milliseconds: Long) {
//        this.timeInMs = milliseconds
//    }
//
//    // Option B: Set directly using an Enum value
//    fun setTime(preset: VideoSpeedPreset) {
//        this.timeInMs = preset.factor
//    }
//}