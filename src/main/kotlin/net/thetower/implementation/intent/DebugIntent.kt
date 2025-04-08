package net.thetower.implementation.intent

import mu.KotlinLogging
import net.thetower.engine.entity.Entity
import net.thetower.engine.state.Effect
import net.thetower.engine.state.Intent
import net.thetower.engine.state.NotificationCategory
import net.thetower.engine.state.NotificationScope

class DebugIntent(
    private val debugMessage: String,
    override val sourceEntity: Entity
) : Intent {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    override val targets: List<Entity> = emptyList()
    override val scope: NotificationScope = NotificationScope.SELF
    override val categories: Set<NotificationCategory> = setOf(NotificationCategory.DEBUG)

    override fun execute(): List<Effect> {
        log.debug("Debug intent executed:$debugMessage")

        return emptyList()
    }

}