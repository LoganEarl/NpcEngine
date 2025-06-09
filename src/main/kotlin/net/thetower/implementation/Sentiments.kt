package net.thetower.implementation

import net.thetower.engine.npc.MoodComponent.ANGER_CALMNESS
import net.thetower.engine.npc.OpinionComponent.APATHY_INTEREST
import net.thetower.engine.npc.OpinionComponent.DISGUST_TRUST
import net.thetower.engine.state.NpcEntityMentalState
import net.thetower.engine.state.Sentiment

val MASSIVE_POSITIVE = 1.0
val MASSIVE_NEGATIVE = -1.0

val MAJOR_POSITIVE = 0.75
val MAJOR_NEGATIVE = -0.75

val SIGNIFICANT_POSITIVE = 0.5
val SIGNIFICANT_NEGATIVE = -0.5

val SMALL_POSITIVE = 0.25
val SMALL_NEGATIVE = -0.25

val NEUTRAL = 0.0

enum class Sentiments(
    override val mentalStateAffinities: NpcEntityMentalState = NpcEntityMentalState(emptyMap(), emptyMap()),
    override val sourceChanges: NpcEntityMentalState = NpcEntityMentalState(emptyMap(), emptyMap()),
    override val targetChanges: NpcEntityMentalState = NpcEntityMentalState(emptyMap(), emptyMap()),
) : Sentiment {
    /** Guidelines:
    - Expressing yourself doesn't have a large impact on your own mood
    - People can't lose their shit from just letting them know you don't like them. It takes an insult to piss somebody off all the way
    - Heartfelt insults/compliments are -1 and 1 in effects. Everything else should scale off of that
     */


    HEARTFELT_INSULT(
        mentalStateAffinities = NpcEntityMentalState( //Have to care to show hatred
            mapOf(ANGER_CALMNESS to MASSIVE_NEGATIVE),
            mapOf(
                DISGUST_TRUST to MASSIVE_NEGATIVE,
                APATHY_INTEREST to MASSIVE_POSITIVE,
            )
        ),
        sourceChanges = NpcEntityMentalState(
            mapOf(ANGER_CALMNESS to SMALL_NEGATIVE), //Venting makes you less angry over time to a point
            emptyMap(),
        ),
        targetChanges = NpcEntityMentalState(
            mapOf(ANGER_CALMNESS to MASSIVE_NEGATIVE), //Nobody likes to be dissed
            mapOf(DISGUST_TRUST to MASSIVE_NEGATIVE), //Don't trust somebody who hates you
        )
    ),
    HEARTFELT_COMPLIMENT(
        mentalStateAffinities = NpcEntityMentalState(
            mapOf(ANGER_CALMNESS to MASSIVE_POSITIVE),
            mapOf(
                DISGUST_TRUST to MASSIVE_POSITIVE,
                APATHY_INTEREST to MASSIVE_POSITIVE
            ),
        ),
        sourceChanges = NpcEntityMentalState(
            mapOf(ANGER_CALMNESS to SMALL_POSITIVE), //Makes you less likely to do this often
            emptyMap()
        ),
        targetChanges = NpcEntityMentalState(
            mapOf(ANGER_CALMNESS to MASSIVE_POSITIVE),
            mapOf(DISGUST_TRUST to MASSIVE_POSITIVE),
        )
    ),
    POLITE_INTEREST(
        mentalStateAffinities = NpcEntityMentalState(
            mapOf(ANGER_CALMNESS to SMALL_POSITIVE),
            mapOf(
                DISGUST_TRUST to SMALL_POSITIVE,
                APATHY_INTEREST to SMALL_POSITIVE
            )
        ),
        targetChanges = NpcEntityMentalState(
            mapOf(ANGER_CALMNESS to SMALL_POSITIVE),
            mapOf(
                APATHY_INTEREST to SMALL_POSITIVE,
                DISGUST_TRUST to SMALL_POSITIVE
            ), //People like it when others show interest in them
        )
    ),
    POLITE_DISINTEREST(
        mentalStateAffinities = NpcEntityMentalState(
            mapOf(ANGER_CALMNESS to SMALL_POSITIVE),
            mapOf(
                DISGUST_TRUST to SMALL_POSITIVE,
                APATHY_INTEREST to SMALL_NEGATIVE
            )
        ),
        targetChanges = NpcEntityMentalState(
            mapOf(ANGER_CALMNESS to NEUTRAL),
            mapOf(APATHY_INTEREST to SMALL_NEGATIVE, DISGUST_TRUST to NEUTRAL),
        )
    ),
    DISDAIN(
        mentalStateAffinities = NpcEntityMentalState(
            mapOf(ANGER_CALMNESS to NEUTRAL),
            mapOf(
                DISGUST_TRUST to MASSIVE_NEGATIVE,
                APATHY_INTEREST to MASSIVE_NEGATIVE,
            ) //I don't like you, and I don't care what you think
        ),
        targetChanges = NpcEntityMentalState(
            mapOf(ANGER_CALMNESS to SIGNIFICANT_NEGATIVE),
            mapOf(
                DISGUST_TRUST to MASSIVE_NEGATIVE,
                APATHY_INTEREST to SIGNIFICANT_NEGATIVE
            ) //Don't trust somebody who hates you
        )
    )

}