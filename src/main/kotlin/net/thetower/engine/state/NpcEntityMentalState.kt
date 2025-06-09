package net.thetower.engine.state

import net.thetower.engine.npc.MoodComponent
import net.thetower.engine.npc.OpinionComponent
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

data class NpcEntityMentalState(
    val moodChanges: Map<MoodComponent, Double>,
    val opinionChanges: Map<OpinionComponent, Double>
) {
    fun euclideanDistanceTo(other: NpcEntityMentalState, fuzzingFactor: Double, random: Random): Double {
        var moodFuzz = if (fuzzingFactor == 0.0) MoodComponent.entries.map { 0.0 } else
            MoodComponent.entries.map { random.nextDouble(-1.0 * fuzzingFactor, fuzzingFactor) }

        var sumOfSquares = MoodComponent.entries
            .mapIndexed { index: Int, it: MoodComponent ->
                ((moodChanges[it] ?: 0.0) - (other.moodChanges[it] ?: 0.0)) + moodFuzz[index]
            }
            .sumOf { it.pow(2) }

        var opinionFuzz = if (fuzzingFactor == 0.0) OpinionComponent.entries.map { 0.0 } else
            OpinionComponent.entries.map { random.nextDouble(-1.0 * fuzzingFactor, fuzzingFactor) }

        sumOfSquares += OpinionComponent.entries
            .mapIndexed { index, it ->
                ((opinionChanges[it] ?: 0.0) - (other.opinionChanges[it] ?: 0.0)) + opinionFuzz[index]
            }
            .sumOf { it.pow(2) }
        return sqrt(sumOfSquares)
    }

}