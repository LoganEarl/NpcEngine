package net.thetower.implementation

import net.thetower.engine.state.SocialCategory

enum class SocialCategories(
    override val startsConversations: Boolean,
    override val endsConversations: Boolean,
): SocialCategory {
    GREETING(true, false),
    COMMENT(true, false),
    AGREEMENT(false, false),
    DISAGREEMENT(false, false),
    DISENGAGEMENT(false, true),
    SILENCE(false, true);

    companion object {
        val RESPONSE_MAPPINGS: Map<SocialCategory, Set<SocialCategory>> = mapOf(
            GREETING to setOf(GREETING, COMMENT, DISENGAGEMENT, SILENCE),
            COMMENT to setOf(COMMENT, AGREEMENT, DISAGREEMENT, SILENCE),
            AGREEMENT to setOf(COMMENT, DISENGAGEMENT, SILENCE),
            DISAGREEMENT to setOf(COMMENT, DISAGREEMENT, DISENGAGEMENT, SILENCE),
            DISENGAGEMENT to setOf(COMMENT, DISENGAGEMENT, SILENCE),
            SILENCE to setOf(COMMENT, DISENGAGEMENT, SILENCE)
        )
    }

    override val validResponses: Set<SocialCategory>
    get() = RESPONSE_MAPPINGS[this] ?: emptySet()

}