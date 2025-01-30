package com.thetower.ai

class Opinion(
        private val opinionComponents: MutableMap<OpinionComponent, Number> =
                OpinionComponent.values().associate { value -> Pair(value, 0) }.toMutableMap()
) {}
