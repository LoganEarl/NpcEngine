package net.thetower.engine.npc

import net.thetower.engine.util.toStringByReflection

class Mood(
    private val moodComponents: MutableMap<MoodComponent, Double> =
        MoodComponent.entries.associateWith { 0.0 }.toMutableMap()
) {
    val components: Map<MoodComponent, Double> get() = moodComponents

    operator fun get(mood: MoodComponent): Double {
        return moodComponents[mood] ?: 0.0
    }

    operator fun set(mood: MoodComponent, value: Double) {
        moodComponents[mood] = value
    }

    override fun toString(): String {
        return this.toStringByReflection()
    }

}
