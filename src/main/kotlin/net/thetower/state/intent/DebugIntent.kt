package net.thetower.state.intent

import net.thetower.entity.Entity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import net.thetower.state.IntentCategory
import net.thetower.state.IntentScope
import net.thetower.state.effect.Effect

class DebugIntent(
    private val debugMessage: String,
    override val sourceEntity: Entity
) : Intent {
    companion object {
        val log: Logger = LoggerFactory.getLogger(DebugIntent::class.java)
    }

    override val targets: List<Entity> = emptyList()
    override val scope: IntentScope = IntentScope.SELF
    override val categories: Set<IntentCategory> = setOf(IntentCategory.DEBUG)

    override fun execute(): List<Effect> {
        log.debug(debugMessage)

        return emptyList()
    }

}