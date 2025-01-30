package state.effect

import state.intent.Intent

interface Effect {
    val sourceIntent: Intent?
    fun apply()
}