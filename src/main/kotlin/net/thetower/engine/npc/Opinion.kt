package net.thetower.engine.npc

class Opinion(
    private val opinionComponents: MutableMap<OpinionComponent, Double> =
        OpinionComponent.entries.associateWith { 0.0 }.toMutableMap()
) {
    operator fun get(component: OpinionComponent) = opinionComponents[component]
    operator fun set(component: OpinionComponent, value: Double) {
        opinionComponents[component] = value
    }

    val byComponent: Map<OpinionComponent, Double>
        get() = this.opinionComponents
}

