package net.thetower.engine.npc

import mu.KotlinLogging
import net.thetower.engine.entity.EntityController
import net.thetower.engine.entity.NpcEntity
import net.thetower.engine.state.*
import net.thetower.implementation.Sentiments
import net.thetower.implementation.SocialCategories
import kotlin.random.Random
import kotlin.uuid.Uuid

/**
 * This controller is designed to be the "default" entity controller for NPCs. It doesn't ever do anything crazy.
 * It just responds with generic social intents, starts some conversations, joins conversations, etc.
 */
class GenericNpcController(
    override var entity: NpcEntity,
    val intentProcessor: IntentProcessor,
    val socialInteractionModel: SocialInteractionModel,
    val conversationRegistry: ConversationRegistry,
    val random: Random
) : EntityController<NpcEntity>, Notification.Listener {
    init {
        entity.subscribe(this)
    }

    companion object {
        val log = KotlinLogging.logger { }
    }

    override val id = Uuid.random()

    override fun notify(notification: Notification) {
        if (notification.intent.sourceEntity != this.entity) {
            if (notification.intent is SocialIntent && notification.intent.targets.contains(this.entity)) {
                handleSocialIntentDirectedAtSelf(notification.intent, notification)
            }
        }
    }

    fun handleSocialIntentDirectedAtSelf(intent: SocialIntent, notification: Notification) {
        //They might have just tried to end the conversation.
        if (intent.socialCategory.endsConversations) {
            //Decide if we let them end it or if we persist
            if (random.nextBoolean()) {
                log.info("${entity.id} has ended their conversation with ${intent.sourceEntity.id}")
                conversationRegistry.leaveConversation(this.entity)
                return
            }
        }

        var conversation = conversationRegistry.lookupConversationByEntityId(entity.id)
            ?: conversationRegistry.startConversation(
                notification.intent.sourceEntity, this.entity
            )
        var randomResponseCategory = intent.socialCategory.validResponses.random(random)

        var possibleSentiments = socialInteractionModel.loadedSentiments
        var currentMentalState = entity.getMentalStateTowards(intent.sourceEntity.id)
        val bestSentiment = possibleSentiments.minBy {
            it.mentalStateAffinities.euclideanDistanceTo(currentMentalState, socialInteractionModel.fuzzingFactor, random)
        }

        conversationRegistry.submitToConversation(
            GenericSocialIntent(
                randomResponseCategory,
                bestSentiment,
                entity,
                listOf(notification.intent.sourceEntity),
                NotificationScope.SUBJECTS,
                setOf(NotificationCategory.SOCIAL),
                socialInteractionModel,
                conversationRegistry
            ),
            conversation,
            ConversationalPriority.RESPONSE_TO_DIRECT_ACTION
        )
    }

    override fun notifyOfTickEnd(tickNumber: Int) {
        //No-op for now. We will need this for the player controller mostly
        // TODO Useful if NPCs need to act on a schedule or do things on their own initiative
    }
}

