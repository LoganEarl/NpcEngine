package net.thetower.engine.npc

import kotlin.math.abs


class SocialInteractionModel {

    val effectsOfPersonalityByMood: List<MoodModifier> = listOf(
        //This means that a contentious person's base mood value will result in them being rather angry
        MoodModifier(
            PersonalityComponent.CONTENTIOUS_EXTOLLING, mapOf(
                Pair(MoodComponent.ANGRY_CALM, .25)
            )
        )
    )

    // A way for personality to effect the base values for the entity's mood
    class MoodModifier(
        val personalityComponent: PersonalityComponent,
        val moodBaseValueMultiplier: Map<MoodComponent, Double>
    )

    fun adjustMood(moodComponent: MoodComponent, amount: Double, mood: Mood, personality: Personality) : Mood {
        val currentMoodValue = mood[moodComponent]
        val baseMoodValue = getBaseMoodValue(moodComponent, personality)
        //Current value as if there was no personality
        val normalizedCurrentValue = currentMoodValue - baseMoodValue
        val newNormalizedValue =
            normalizedCurrentValue + amount * (1.0 - abs(normalizedCurrentValue))

        mood[moodComponent] = newNormalizedValue + baseMoodValue
        return mood
    }

    private fun getBaseMoodValue(component: MoodComponent, personality: Personality): Double {
        return effectsOfPersonalityByMood
            .filter { modifier -> modifier.moodBaseValueMultiplier.containsKey(component) }
            .sumOf {
                val scalar: Double = it.moodBaseValueMultiplier[component] ?: 0.0
                val personalityValue: Double = personality[it.personalityComponent] ?: 0.0
                scalar * personalityValue
            }
    }
}
