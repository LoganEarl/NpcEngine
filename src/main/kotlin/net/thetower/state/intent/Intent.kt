package net.thetower.state.intent

import net.thetower.entity.Entity
import net.thetower.state.effect.Effect
import net.thetower.state.IntentCategory
import net.thetower.state.IntentScope

interface Intent {
    val sourceEntity: Entity
    val targets: List<Entity>
    val scope: IntentScope
    val categories: Set<IntentCategory>

    fun execute(): List<Effect>
}