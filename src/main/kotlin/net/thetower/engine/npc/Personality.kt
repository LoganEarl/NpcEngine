package net.thetower.engine.npc

class Personality(
        private val personalityComponents: MutableMap<PersonalityComponent, Double> =
                PersonalityComponent.entries.associateWith { 0.0 }.toMutableMap()
) {
        operator fun get(personality: PersonalityComponent) = personalityComponents[personality]
}
