package net.thetower.state.effect

import com.sun.org.slf4j.internal.Logger
import com.sun.org.slf4j.internal.LoggerFactory
import net.thetower.state.intent.Intent

class DebugEffect(
    private val debugText: String,
    override val sourceIntent: Intent
) : Effect {
    companion object {
        val log: Logger = LoggerFactory.getLogger(DebugEffect::class.java)
    }

    override fun apply() {
        log.debug(debugText)
    }


}