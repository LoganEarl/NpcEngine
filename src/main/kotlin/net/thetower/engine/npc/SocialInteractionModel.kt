package net.thetower.engine.npc

import mu.KotlinLogging
import net.thetower.engine.entity.Entity
import net.thetower.engine.entity.NpcEntity
import net.thetower.engine.state.NpcEntityMentalState
import net.thetower.engine.state.Sentiment
import net.thetower.engine.state.SocialCategory
import net.thetower.engine.state.SocialEffect
import net.thetower.implementation.SocialCategories
import kotlin.random.Random


//TODO move this whole thing out of the engine, and make an interface for it to keep in the engine. this is all implementation-specific stuff and shouldn't be here
class SocialInteractionModel(
    val loadedSocialCategories: Set<SocialCategory> = SocialCategories.entries.toSet(),
    val loadedSentiments: Set<Sentiment>,
    val random: Random
) {
    val fuzzingFactor = 0.0

    //How many ticks it would take to reach the associated feeling. Big number = slow change
    val moodChangeRate = 2.0
    val opinionChangeRate = 5.0

    companion object {
        val log = KotlinLogging.logger { }
    }

    private val effectsOfPersonalityByMood: List<MoodModifier> = listOf(
        //Bias extolling people toward calmness and contentious people toward anger
        MoodModifier(
            PersonalityComponent.CONTENTIOUS_EXTOLLING, mapOf(
                Pair(MoodComponent.ANGER_CALMNESS, 1.0)
            )
        )
    )

    // A way for personality to effect the base values for the entity's mood
    private class MoodModifier(
        val personalityComponent: PersonalityComponent,
        val moodBaseValueMultiplier: Map<MoodComponent, Double>
    )

    class BystanderReaction(
        val reactionToSource: NpcEntityMentalState,
        val reactionToTargets: NpcEntityMentalState, //Responds to all targets equally... oversimplified but this shit is complicated enough lol
    )

    fun adjustMood(effect: SocialEffect, mood: Mood, personality: Personality) {
        for (change in effect.moodChanges) {
            val moodComponent = change.key
            val changeMagnitude = change.value
            val currentMoodValue = mood[moodComponent]
            val baseMoodValue = getBaseMoodValue(moodComponent, personality)
            //Current value as if there was no personality
            val normalizedCurrentValue = currentMoodValue - baseMoodValue
            val newNormalizedCurrentValue =
                normalizedCurrentValue + (changeMagnitude - normalizedCurrentValue)/moodChangeRate
            val sameSign = changeMagnitude * normalizedCurrentValue > 0

            //If they did something nice, but our rep is greater than the nice thing's magnitude, don't hurt it and
            // just keep the current value. Don't punish people for being polite to somebody that already likes them
            if (sameSign && changeMagnitude < normalizedCurrentValue) {
                mood[moodComponent] = currentMoodValue
            } else {
                mood[moodComponent] = newNormalizedCurrentValue + baseMoodValue
            }
        }
    }

    fun adjustOpinion(effect: SocialEffect, opinionsOfSourceEntity: Opinion) {
        for (change in effect.opinionComponentChanges) {
            val opinionComponent = change.key
            val changeMagnitude = change.value
            val currentOpinionValue = opinionsOfSourceEntity[opinionComponent] ?: 0.0
            val newOpinionValue = currentOpinionValue + (changeMagnitude - currentOpinionValue)/opinionChangeRate

            val sameSign = changeMagnitude * currentOpinionValue > 0
            if (sameSign && changeMagnitude < currentOpinionValue) {
                opinionsOfSourceEntity[opinionComponent] = currentOpinionValue
            } else {
                opinionsOfSourceEntity[opinionComponent] = newOpinionValue
            }
        }
    }

    /**
     * If person A calls person B a bitch, a third party C should have a reaction. Their reaction should be based off
     * whether they like A or B more, and by how much they like them.
     *
     * Returns the effect to the bystander's mood and opinions
     */
    fun getBystandersReactionToSentiment(
        bystander: NpcEntity,
        source: Entity,
        targets: List<Entity>,
        sentiment: Sentiment
    ): BystanderReaction {
        // Will be -1 to 1. Positive means we generally like them (ish) and negative means generally dislike
        val attachmentToSource = bystander.opinionsByEntityId[source.id]?.components?.values?.average() ?: 0.0
        val reactionToSource = scaleChangesAccordingToAttachment(sentiment.sourceChanges, attachmentToSource)

        val averageAttachmentToTargets = targets.map {
            bystander.opinionsByEntityId[it.id]?.components?.values?.average() ?: 0.0
        }.average()
        val reactionToTargets = scaleChangesAccordingToAttachment(sentiment.targetChanges, averageAttachmentToTargets)

        return BystanderReaction(reactionToSource, reactionToTargets)
    }

    private fun scaleChangesAccordingToAttachment(
        changes: NpcEntityMentalState,
        attachment: Double
    ): NpcEntityMentalState {
        //If value is > 0 we should try to mirror the source's mental state due to empathy. < 0 means we should have the opposite reaction
        val opinionChanges = changes.opinionChanges.entries
            .associate { entry -> entry.key to entry.value * attachment }
        val moodChanges = changes.moodChanges.entries
            .associate { entry -> entry.key to entry.value * attachment }
        return NpcEntityMentalState(moodChanges, opinionChanges)
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
