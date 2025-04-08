package net.thetower.implementation.effect

import net.thetower.engine.entity.Entity
import net.thetower.engine.npc.MoodComponent
import net.thetower.engine.npc.OpinionComponent
import net.thetower.engine.state.Effect
import net.thetower.engine.state.Intent
import net.thetower.engine.state.NotificationCategory
import net.thetower.engine.state.NotificationScope

class SocialEffect (
    val opinionChanges: Map<OpinionComponent, Double>,
    val moodChanges: Map<MoodComponent, Double>,
    override val sourceIntent: Intent?,
    override val target: Entity
) : Effect {
    override val categories: Set<NotificationCategory> = setOf(NotificationCategory.SOCIAL)

    override fun apply() {
        //This is actually a no-op. After all, social exchanges don't do much directly.
        // It is mostly up to each AI controller to determine what to do in a given situation
        // TODO actually, this should be where I adjust opinions and stuff. This is a better place then the controller IMO.
        //  Opinions count as state after all
    }
}