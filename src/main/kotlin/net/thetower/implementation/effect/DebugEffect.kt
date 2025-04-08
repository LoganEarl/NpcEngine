package net.thetower.implementation.effect

import mu.KotlinLogging
import net.thetower.engine.entity.Entity
import net.thetower.engine.state.Effect
import net.thetower.engine.state.Intent
import net.thetower.engine.state.NotificationCategory

class DebugEffect(
    private val debugText: String,
    override val sourceIntent: Intent,
    override val target: Entity
) : Effect {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    override val categories = setOf(NotificationCategory.DEBUG)

    override fun apply() {
        log.debug("Debug effect triggered:$debugText")
    }
}