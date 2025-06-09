package net.thetower.engine.state

import mu.KotlinLogging
import net.thetower.engine.entity.Entity
import net.thetower.engine.entity.NpcEntity
import net.thetower.engine.npc.MoodComponent
import net.thetower.engine.npc.Opinion
import net.thetower.engine.npc.OpinionComponent
import net.thetower.engine.npc.SocialInteractionModel

class SocialEffect(
    val socialCategory: SocialCategory,
    val opinionComponentChanges: Map<OpinionComponent, Double>,
    val moodChanges: Map<MoodComponent, Double>,
    val socialInteractionModel: SocialInteractionModel,
    val subject: Entity, //Who is being talked about
    override val target: Entity, //Whom is being talked to.
    override val sourceIntent: Intent?, //Who is doing the talking
    override val categories: Set<NotificationCategory>,
) : Effect {
    companion object {
        val log = KotlinLogging.logger { }
    }
    override fun apply() {
        if(target is NpcEntity && sourceIntent is SocialIntent) {
            if(opinionComponentChanges.isNotEmpty()) {
                val targetOpinionOfSubject = target.opinionsByEntityId.getOrPut(subject.id) { Opinion() }
                socialInteractionModel.adjustOpinion(this, targetOpinionOfSubject)
            }
            if(moodChanges.isNotEmpty()) {
                socialInteractionModel.adjustMood(this, target.mood, target.personality)
            }
        }
    }

}