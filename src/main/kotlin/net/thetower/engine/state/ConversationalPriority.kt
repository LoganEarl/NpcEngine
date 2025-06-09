package net.thetower.engine.state

enum class ConversationalPriority(
    val canCoexistWithOtherStatements: Boolean = false
) {
    PASSIVE_INTERJECTION(true),
    RESPONSE_TO_UNRELATED_ACTION,
    RESPONSE_TO_INDIRECT_ACTION,
    RESPONSE_TO_DIRECT_ACTION,
    INTERRUPTION;
}