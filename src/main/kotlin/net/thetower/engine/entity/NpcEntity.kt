package net.thetower.engine.entity

import mu.KotlinLogging
import net.thetower.engine.npc.Mood
import net.thetower.engine.npc.Opinion
import net.thetower.engine.npc.Personality
import net.thetower.engine.state.Notification
import net.thetower.engine.state.NpcEntityMentalState
import net.thetower.engine.util.toStringByReflection
import kotlin.uuid.Uuid

class NpcEntity(
   override val id: Uuid,
   val personality: Personality = Personality(),
   val mood: Mood = Mood(),
   val opinionsByEntityId: MutableMap<Uuid, Opinion> = mutableMapOf()
) : Entity {
   override val subscribers: MutableMap<Uuid, Notification.Listener> = mutableMapOf()

   companion object {
      private val log = KotlinLogging.logger {}
   }

   override fun notify(notification: Notification) {
      subscribers.values.forEach { it.notify(notification) }
   }

   override fun notifyOfTickEnd(tickNumber: Int) {
      subscribers.values.forEach { it.notifyOfTickEnd(tickNumber) }
   }

   fun getMentalStateTowards(entityId: Uuid): NpcEntityMentalState {
      val opinion = opinionsByEntityId[entityId] ?: Opinion()
      return NpcEntityMentalState(mood.components, opinion.components)
   }

   override fun toString(): String {
      return this.toStringByReflection()
   }
}