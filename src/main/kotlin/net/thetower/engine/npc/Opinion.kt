package net.thetower.engine.npc

import net.thetower.engine.util.toStringByReflection

class Opinion(
    private val opinionComponents: MutableMap<OpinionComponent, Double> =
        OpinionComponent.entries.associateWith { 0.0 }.toMutableMap()
) {
    operator fun get(component: OpinionComponent) = opinionComponents[component]
    operator fun set(component: OpinionComponent, value: Double) {
        opinionComponents[component] = value
    }

    val components: Map<OpinionComponent, Double>
        get() = this.opinionComponents

    override fun toString(): String {
        return this.toStringByReflection()
    }
}

