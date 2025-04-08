package net.thetower.implementation.intent

import mu.KotlinLogging
import net.thetower.engine.entity.Entity
import net.thetower.engine.npc.MoodComponent
import net.thetower.engine.npc.OpinionComponent
import net.thetower.engine.state.Effect
import net.thetower.engine.state.Intent
import net.thetower.engine.state.NotificationCategory
import net.thetower.engine.state.NotificationScope
import net.thetower.implementation.effect.SocialEffect
import org.kodein.di.DirectDI
import org.kodein.di.instance
import kotlin.random.Random

class InsultIntent(
    override val sourceEntity: Entity,
    override val targets: List<Entity>,
    di: DirectDI
) : Intent {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    override val scope = NotificationScope.SUBJECT_LOCALES
    override val categories = setOf(NotificationCategory.SOCIAL)
    private val random: Random = di.instance()

    override fun execute(): List<Effect> {

        val score = random.nextInt(10)

        return if (score > 1) {
            log.info("Entity ${sourceEntity.id} gives insult to targets: ${targets.map { it.id }} with score: $score")
            targets.map { effectTarget ->
                SocialEffect(
                    mapOf(
                        Pair(OpinionComponent.DISGUST_TRUST, -.1)
                    ), mapOf(
                        Pair(MoodComponent.ANGRY_CALM, -0.5)
                    ),
                    this, effectTarget
                )
            }
        } else {
            log.info{"Entity ${sourceEntity.id} fails to give insult to targets: ${targets.map { it.id }} with score: $score"}
            //TODO figure out what should happen if someone fucks up when giving an insult
            emptyList()
        }
    }
}