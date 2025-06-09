package net.thetower.engine.npc

import net.thetower.engine.util.toStringByReflection

class Personality(
    private val personalityComponents: MutableMap<PersonalityComponent, Double> =
        PersonalityComponent.entries.associateWith { 0.0 }.toMutableMap()
) {
    operator fun get(personality: PersonalityComponent) = personalityComponents[personality]

    override fun toString(): String {
        return this.toStringByReflection()
    }
}
