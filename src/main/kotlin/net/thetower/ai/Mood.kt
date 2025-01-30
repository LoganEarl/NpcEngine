package com.thetower.ai

class Mood(
        private val moodComponents: MutableMap<MoodComponent, Number> =
                MoodComponent.values().associate { value -> Pair(value, 0) }.toMutableMap()
) {}
