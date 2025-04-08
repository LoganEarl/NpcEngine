package net.thetower.engine.npc

class Mood(
    private val moodComponents: MutableMap<MoodComponent, Double> =
        MoodComponent.entries.associateWith { 0.0 }.toMutableMap()
) {
    operator fun get(mood: MoodComponent): Double {
        return moodComponents[mood] ?: 0.0
    }

    operator fun set(mood: MoodComponent, value: Double) {
        moodComponents[mood] = value
    }
}
