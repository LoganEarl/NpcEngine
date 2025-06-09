package net.thetower.engine.state

import net.thetower.engine.entity.Entity
import kotlin.uuid.Uuid

class Conversation(
    val id: Uuid,
    initiator: Entity,
    recipients: Set<Entity>,
) {
    private val currentMembers: MutableSet<Entity> = mutableSetOf()
    private val historyByTickNumber: MutableMap<Int, MutableSet<Notification>> = mutableMapOf()

    val participants: Set<Entity> = currentMembers

    init {
        currentMembers.add(initiator)
        currentMembers.addAll(recipients)
    }

    fun addExchange(notification: Notification) {
        if (historyByTickNumber[notification.tick] != null) {
            historyByTickNumber[notification.tick]?.add(notification)
        } else {
            historyByTickNumber.put(notification.tick, mutableSetOf(notification))
        }
    }

    fun addPassiveMember(entity: Entity) {
        currentMembers.add(entity)
    }

    fun removeMember(entity: Entity) {
        currentMembers.remove(entity)
    }
}