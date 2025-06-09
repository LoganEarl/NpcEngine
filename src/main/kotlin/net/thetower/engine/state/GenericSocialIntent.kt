package net.thetower.engine.state

import mu.KotlinLogging
import net.thetower.engine.entity.Entity
import net.thetower.engine.entity.NpcEntity
import net.thetower.engine.npc.SocialInteractionModel

class GenericSocialIntent(
    override val socialCategory: SocialCategory,
    override val sentiment: Sentiment,
    override val sourceEntity: Entity,
    override val targets: List<Entity>,
    override val scope: NotificationScope,
    override val categories: Set<NotificationCategory>,
    override val socialInteractionModel: SocialInteractionModel,
    val conversationRegistry: ConversationRegistry
) : SocialIntent {
    companion object {
        val log = KotlinLogging.logger { }
    }

    override fun execute(): List<Effect> {
        log.info("Entity ${sourceEntity.id} opts to $socialCategory with $sentiment to ${targets.map { it.id }}")
        return produceTargetEffects() + produceSourceEffects() + produceBystanderEffects()
    }

    private fun produceTargetEffects() : List<Effect> {
        return targets.map { target ->
            SocialEffect(
                socialCategory = socialCategory,
                opinionComponentChanges = sentiment.targetChanges.opinionChanges,
                moodChanges = sentiment.targetChanges.moodChanges,
                socialInteractionModel = socialInteractionModel,
                subject = sourceEntity, //Generic social doesn't support talking about 3rd parties
                target = target,
                sourceIntent = this,
                categories = categories,
            )
        }
    }

    private fun produceSourceEffects() : List<Effect> {
        return effectPerTarget(sourceEntity, targets, sentiment.sourceChanges)
    }

    private fun effectPerTarget(sourceEntity: Entity, targets: List<Entity>, changes: NpcEntityMentalState) : List<Effect> {
        val opinionEffects = targets.map { target ->
            SocialEffect(
                socialCategory = socialCategory,
                opinionComponentChanges = changes.opinionChanges,
                moodChanges = emptyMap(),
                socialInteractionModel = socialInteractionModel,
                subject = target,
                target = sourceEntity,
                sourceIntent = this,
                categories = categories
            )
        }
        //Mood is seperated so we don't stack a mood change per target. Would magnify the effect, which we don't want
        val moodEffect = SocialEffect(
            socialCategory = socialCategory,
            opinionComponentChanges = emptyMap(),
            moodChanges = changes.moodChanges,
            socialInteractionModel = socialInteractionModel,
            subject = sourceEntity,
            target = sourceEntity,
            sourceIntent = this,
            categories = categories
        )
        return opinionEffects + moodEffect
    }

    /**
     * If there are other people in the conversation, and they aren't the targets, they might get reduced version of the
     * target effects based on what they think of each target, and what they think of the source. In other words, nobody
     * likes it when a rando calls their bestie a bitch right in front of them. They should mirror their bestie's
     * feelings of the rando and feel the opposite of the rando's feelings toward their bestie
     */
    private fun produceBystanderEffects() : List<Effect> {
        val conversation = conversationRegistry.byId[sourceEntity.id]
        if(conversation == null) return emptyList()

        val bystanderNpcs: List<NpcEntity> = conversation.participants
            .filter { it != sourceEntity }
            .filter { !targets.contains(it) }
            .filter { it is NpcEntity }
            .map { it as NpcEntity }

        val bystanderReactions = bystanderNpcs
            .map { Pair(it, socialInteractionModel.getBystandersReactionToSentiment(it, sourceEntity, targets, sentiment)) }
        val bystanderSourceEffects = bystanderReactions
            .map { it.second.reactionToSource }
            .map { effect -> SocialEffect(
                socialCategory = socialCategory,
                opinionComponentChanges = effect.opinionChanges,
                moodChanges = effect.moodChanges,
                socialInteractionModel = socialInteractionModel,
                subject = sourceEntity,
                target = sourceEntity,
                sourceIntent = this,
                categories = categories,
            ) }
        val bystanderTargetEffects = bystanderReactions
            .flatMap { effectPerTarget(it.first, targets, it.second.reactionToTargets) }

        return bystanderTargetEffects + bystanderSourceEffects
    }
}