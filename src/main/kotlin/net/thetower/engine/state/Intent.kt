package net.thetower.engine.state

import net.thetower.engine.entity.Entity


interface Intent {
    val sourceEntity: Entity
    val targets: List<Entity>
    val scope: NotificationScope
    val categories: Set<NotificationCategory>

    fun execute(): List<Effect>
}