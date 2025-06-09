package net.thetower.implementation

import net.thetower.engine.entity.Entity
import net.thetower.engine.locale.Locale
import net.thetower.engine.locale.LocaleModel
import net.thetower.engine.state.ConversationRegistry
import kotlin.uuid.Uuid

/**
 * A model of the locale system that puts people who are in conversations together into the same room. They will only be near
 * each other if they are in a conversation. This is mostly used just for testing conversation statistics
 */
class ConversationalRoomModel(
    private val entities: Set<Entity>
) : LocaleModel {
    override val localesById: Map<Uuid, Locale> = mutableMapOf()
    var conversationRegistry: ConversationRegistry? = null

    override fun getEntitiesNearby(entity: Entity): Set<Entity> {
        return conversationRegistry?.lookupConversationByEntityId(entity.id)?.participants ?: emptySet()
    }

    override fun getAllActiveEntities(): Set<Entity> {
        return entities
    }


}