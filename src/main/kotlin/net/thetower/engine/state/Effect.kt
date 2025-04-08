package net.thetower.engine.state

import net.thetower.engine.entity.Entity


/**
 * Effects should change the game state. They do not have to worry about notifications though, that is handled elsewhere.
 */
interface Effect {
    val sourceIntent: Intent?
    val categories: Set<NotificationCategory>
    val target: Entity
    fun apply()
}