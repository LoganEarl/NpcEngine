package net.thetower.engine.state

interface Sentiment {
    val mentalStateAffinities: NpcEntityMentalState
    val sourceChanges: NpcEntityMentalState
    val targetChanges: NpcEntityMentalState
}