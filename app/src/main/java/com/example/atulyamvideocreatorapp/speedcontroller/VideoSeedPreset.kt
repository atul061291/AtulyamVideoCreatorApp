package com.example.atulyamvideocreatorapp.speedcontroller

enum class VideoSpeedPreset(val label: String, val factor: Float, val isAdvanced: Boolean) {
    SLOW1("Time (100ms)", 0.1f, isAdvanced = false),
    SLOW2("Time (200ms)", 0.2f, isAdvanced = false),
    SLOW3("Time (300ms)", 0.3f, isAdvanced = false),
    SLOW4("Time (400ms)", 0.4f, isAdvanced = false),
    SLOW5("Time (0.5s)", 0.5f, isAdvanced = true),
    SLOW(
        "Time (600ms)",
        0.6f,
        isAdvanced = false
    ),     // 0.6x = 40% slower, highly cinematic and pleasant
    SLOW_PRO1("Time (700ms)", 0.1f, isAdvanced = false),
    SLOW_PRO2("Time (800ms)", 0.2f, isAdvanced = false),
    SLOW_PRO3("Time (900ms)", 0.3f, isAdvanced = false),
    NORMAL("Time (1s)", 1.0f, isAdvanced = true), // 1.0x = Standard baseline timing
    NORMAL1("Time (1100ms)", 1.1f, isAdvanced = false),
    NORMAL2("Time (1200ms)", 1.2f, isAdvanced = false),
    NORMAL3("Time (1300ms)", 1.3f, isAdvanced = false),
    NORMAL4("Time (1400ms)", 1.4f, isAdvanced = false),

    FAST(
        "Time (5s)",
        1.5f,
        isAdvanced = true
    ),      // 1.5x = Fast cuts, high energy template rhythm
    FAST1("Time (1600ms)", 1.6f, isAdvanced = false),
    FAST2("Time (1700ms)", 1.7f, isAdvanced = false),
    FAST3("Time (1800ms)", 1.8f, isAdvanced = false),
    FAST4("Time (1900ms)", 1.9f, isAdvanced = false),

    FASTER(
        "Time (2s)",
        2.0f,
        isAdvanced = true
    ),      // 1.5x = Fast cuts, high energy template rhythm
    FASTER1("Time (2100ms)", 2.1f, isAdvanced = false),
    FASTER2("Time (2200ms)", 2.2f, isAdvanced = false),
    FASTER3("Time (2300ms)", 2.3f, isAdvanced = false),
    FASTER4("Time (2400ms)", 2.4f, isAdvanced = false),

    FASTEST(
        "Time (2.5s)",
        2.5f,
        isAdvanced = true
    ),     // 1.5x = Faster cuts, high energy template rhythm
    FASTEST1("Time (2600ms)", 2.6f, isAdvanced = false),
    FASTEST2("Time (2700ms)", 2.7f, isAdvanced = false),
    FASTEST3("Time (2800ms)", 2.8f, isAdvanced = false),
    FASTEST4("Time (2900ms)", 2.9f, isAdvanced = false),

    X3FAST(
        "3XFast (3s)",
        3.0f,
        isAdvanced = true
    ),     //  3.0x = Super Fast cuts, high energy template rhythm
    X3FAST1("Time (3100ms)", 3.1f, isAdvanced = false),
    X3FAS2("Time (3200ms)", 3.2f, isAdvanced = false),
    X3FAS3("Time (3300ms)", 3.3f, isAdvanced = false),
    X3FAST4("Time (3400ms)", 3.4f, isAdvanced = false),
    X3FAST_PRO("Time (3.5s)", 3.5f, isAdvanced = true),
    X3FAST_PRO1("Time (3600ms)", 3.6f, isAdvanced = false),
    X3FAST_PRO2("Time (3700ms)", 3.7f, isAdvanced = false),
    X3FAST_PRO3("Time (3800ms)", 3.8f, isAdvanced = false),
    X3FAST_PRO4("Time (3900ms)", 3.9f, isAdvanced = false),

    X4FAST(
        "Time (4s)",
        4.0f,
        isAdvanced = true
    ),     //  4.0x = Super Fast cuts, high energy template rhythm
    X4FAST1("Time (4100ms)", 4.1f, isAdvanced = false),
    X4FAS2("Time (4200ms)", 4.2f, isAdvanced = false),
    X4FAS3("Time (4300ms)", 4.3f, isAdvanced = false),
    X4FAST4("Time (4400ms)", 4.4f, isAdvanced = false),
    X4FAST_PRO("Time (4500ms)", 4.5f, isAdvanced = true),
    X4FAST_PRO1("Time (4600ms)", 4.6f, isAdvanced = false),
    X4FAST_PRO2("Time (4700ms)", 4.7f, isAdvanced = false),
    X4FAST_PRO3("Time (4800ms)", 4.8f, isAdvanced = false),
    X4FAST_PRO4("Time (4900ms)", 4.9f, isAdvanced = false),

    //  5.0x = Super Fast cuts, high energy template rhythm
    X5FAST("Time (5s)", 5.0f, isAdvanced = true),
    X6FAST("Time (6s)", 6.0f, isAdvanced = true),
    X7FAST("Time (7s)", 7.0f, isAdvanced = true),
    X8FAST("Time (8s)", 8.0f, isAdvanced = true),
    X9FAST("Time (9s)", 9.0f, isAdvanced = true),
    X10FAST("Time (10s)", 10.0f, isAdvanced = true),
    X11FAST("Time (11s)", 11.0f, isAdvanced = true),
    X12FAST("Time (12s)", 12.0f, isAdvanced = true),
    X15FAST("Time (15s)", 15.0f, isAdvanced = true),
    X30FAST("Time (30s)", 30.0f, isAdvanced = true),
    X45FAST("Time (45s)", 45.0f, isAdvanced = true),
    X60FAST("Time (60s)", 60.0f, isAdvanced = true);

    companion object {
        // Filter out normal features
        fun getSingleClickFeatures(): List<VideoSpeedPreset> =
            entries.filter { !it.isAdvanced }

        // Filter out advanced features
        fun getDoubleClickFeatures(): List<VideoSpeedPreset> =
            entries.filter { it.isAdvanced }
    }
}
