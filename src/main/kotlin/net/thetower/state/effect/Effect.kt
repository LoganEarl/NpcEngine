package net.thetower.state.effect

import net.thetower.state.intent.Intent

interface Effect {
    val sourceIntent: Intent?
    fun apply()
}