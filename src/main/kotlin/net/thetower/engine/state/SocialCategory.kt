package net.thetower.engine.state

interface SocialCategory {
    val validResponses: Set<SocialCategory>
    val startsConversations: Boolean
    val endsConversations: Boolean
}