package net.thetower.engine.state

import mu.KotlinLogging
import net.thetower.engine.entity.Entity
import java.util.concurrent.ConcurrentHashMap
import kotlin.uuid.Uuid

class ConversationRegistry(
    val intentProcessor: IntentProcessor
) : Notification.Listener {
    init {
        //We need to know when the tick ends
        intentProcessor.subscribe(this)
    }

    companion object {
        val log = KotlinLogging.logger {}
    }

    override val id: Uuid = Uuid.random();
    private val stringLocks = ConcurrentHashMap<Uuid, Any>()
    private val conversationsById: MutableMap<Uuid, Conversation> = mutableMapOf()
    private val conversationsByEntityId: MutableMap<Uuid, Conversation> = mutableMapOf()
    private val submissionsByConversationId: MutableMap<Uuid, MutableList<ConversationSubmission>> = mutableMapOf()

    val hasActiveConversations: Boolean
        get() = conversationsByEntityId.isNotEmpty()

    private data class ConversationSubmission(
        val intent: SocialIntent,
        val priority: ConversationalPriority
    )

    val byId: Map<Uuid, Conversation> = conversationsById

    private fun <T> synchronizeOnId(id: Uuid, block: () -> T): T {
        val lock = stringLocks.computeIfAbsent(id) { Any() }
        synchronized(lock) {
            return block()
        }
    }

    fun lookupConversationByEntityId(id: Uuid): Conversation? {
        return conversationsByEntityId[id]
    }

    //Note, conversations are started by whomever first responds. If somebody calls out and you don't answer, a conversation
    //is never created
    fun startConversation(initiator: Entity, target: Entity): Conversation {
        val conversationId = (conversationsByEntityId[target.id] ?: conversationsByEntityId[initiator.id])?.id
            ?: Uuid.random()
        val newConversation = Conversation(conversationId, initiator, setOf(target))
        return synchronizeOnId(conversationId) {
            check(conversationsById[conversationId] == null) {
                "Entity ${initiator.id} cannot start a conversation with ${target.id} because target conversation id: $conversationId already exists"
            }
            conversationsByEntityId[target.id] = newConversation
            conversationsByEntityId[initiator.id] = newConversation
            conversationsById[conversationId] = newConversation
            newConversation
        }
    }

    fun joinConversation(joiner: Entity, memberOfConversation: Entity) {
        val existingConversationId = conversationsByEntityId[memberOfConversation.id]?.id ?: Uuid.random()

        synchronizeOnId(existingConversationId) {
            check(conversationsByEntityId[memberOfConversation.id] != null) {
                "${joiner.id} tried to join a conversation with ${memberOfConversation.id} but they were not in an existing conversation"
            }

            var conversation = conversationsById[existingConversationId]!!
            conversation.addPassiveMember(joiner)
            conversationsByEntityId[joiner.id] = conversation
        }
    }

    fun leaveConversation(leaver: Entity) {
        val existingConversation = conversationsByEntityId[leaver.id]
        if (existingConversation != null) {
            existingConversation.removeMember(leaver)
            conversationsByEntityId.remove(leaver.id)
            log.info("Entity ${leaver.id} left conversation ${existingConversation.id}")
            //One person can't be in a conversation by themselves
            if (existingConversation.participants.size == 1) {
                conversationsByEntityId.remove(existingConversation.participants.first().id)
                conversationsById.remove(existingConversation.id)
                submissionsByConversationId.remove(existingConversation.id)
                log.info("Conversation ${existingConversation.id} has ended due to not enough participants")
            }
        }
    }

    fun submitToConversation(intent: SocialIntent, conversation: Conversation, priority: ConversationalPriority) {
        val currentSubmissions = submissionsByConversationId.computeIfAbsent(conversation.id) { mutableListOf() }
        currentSubmissions.add(ConversationSubmission(intent, priority))
    }

    override fun notify(notification: Notification) {}

    override fun notifyOfTickEnd(tickNumber: Int) {
        //Need to submit intents that got stacked up based on priorities
        for (conversationSubmissions in submissionsByConversationId.entries) {
            //Find the highest priority submission, and ones that are applicable
            val highestPriority: ConversationSubmission = conversationSubmissions.value.maxBy { it.priority }
            val toSubmit: MutableSet<SocialIntent> = mutableSetOf(
                highestPriority.intent,
            )
            toSubmit.addAll(
                conversationSubmissions.value
                    .filter { it.priority.canCoexistWithOtherStatements }
                    .map { it.intent }
            )
            toSubmit.forEach { intentProcessor.enqueueIntent(it) }
        }
        submissionsByConversationId.clear()
    }
}