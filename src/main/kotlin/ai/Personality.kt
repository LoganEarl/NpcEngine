package com.thetower.ai

class Personality(
        private val personalityComponents: MutableMap<PersonalityComponent, Number> =
                PersonalityComponent.values().associate { value -> Pair(value, 0) }.toMutableMap()
) {}
