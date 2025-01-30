package net.thetower.state

import net.thetower.state.effect.Effect
import net.thetower.state.intent.Intent

/**
 * This class is responsible for receiving
 */
class GameEngine {
    private var nextIntents: MutableList<Intent> = mutableListOf()

    fun enqueueIntent(intent: Intent) {
        nextIntents.add(intent)
    }

    @Synchronized
    fun tick() {
        val intents = nextIntents
        nextIntents = mutableListOf()

        val effects: MutableList<Effect> = mutableListOf()
        for (intent in intents) {
            effects.addAll(intent.execute())
        }

        for (effect in effects) {
            effect.apply()
        }
    }

}