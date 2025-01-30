package state.intent

import entity.Entity
import state.effect.Effect
import state.IntentCategory
import state.IntentScope

interface Intent {
    val sourceEntity: Entity
    val targets: List<Entity>
    val scope: IntentScope
    val categories: Set<IntentCategory>

    fun execute(): List<Effect>
}