package net.thetower.engine.entity

import mu.KotlinLogging
import net.thetower.engine.npc.Mood
import net.thetower.engine.npc.Opinion
import net.thetower.engine.npc.Personality
import net.thetower.engine.npc.SocialInteractionModel
import net.thetower.engine.state.Intent
import net.thetower.engine.state.Notification
import net.thetower.implementation.effect.SocialEffect
import kotlin.math.abs
import kotlin.uuid.Uuid

class NpcEntity(
   override val id: Uuid,
   // The intent here is to make as much of the actual numbers changing be determined by this model.
   private val socialModel: SocialInteractionModel,
   private val personality: Personality = Personality(),
   private val mood: Mood = Mood(),
   private val opinionsByEntityId: MutableMap<Uuid, Opinion> = mutableMapOf()
) : Entity {
   override val subscribers: MutableMap<Uuid, Notification.Listener> = mutableMapOf()

   companion object {
      private val log = KotlinLogging.logger {}
   }

   override fun notify(notification: Notification) {
      processSocialImplications(notification)

      subscribers.values.forEach { it.notify(notification) }
   }

   override fun notifyOfTickEnd(tickNumber: Int) {
      subscribers.values.forEach { it.notifyOfTickEnd(tickNumber) }
   }

   fun processSocialImplications(notification: Notification) {
      for (effect in notification.effects) {
         if (effect is SocialEffect) {
            processSocialEffect(notification.intent, effect)
         }
      }

      //TODO Eventually store some kind of memory of the event... not sure what that would look like yet

      //TODO Determine social response category or categories. Not sure if we should pick one or multiple...

   }

   fun processSocialEffect(intent: Intent, effect: SocialEffect) {
      if (intent.sourceEntity == this) {
         return
      }
      //If I am the target, it should effect my mood and opinions directly
      if (effect.target == this) {
         //Change my mood as a result of the action
         effect.moodChanges.forEach { mood ->
            socialModel.adjustMood(mood.key, mood.value, this.mood, this.personality)
         }

         //If an entity produced the effect, change opinions of them as well
         if (effect.sourceIntent != null) {
            val opinionsOfSourceEntity =
               this.opinionsByEntityId[effect.sourceIntent.sourceEntity.id] ?: Opinion()
            effect.opinionChanges.forEach { opinion ->
               val currentOpinionValue = opinionsOfSourceEntity[opinion.key] ?: 0.0
               val changeMagnitude = opinion.value

               opinionsOfSourceEntity[opinion.key] =
                  currentOpinionValue + changeMagnitude * (1.0 - abs(currentOpinionValue))
               this.opinionsByEntityId[effect.sourceIntent.sourceEntity.id] =
                  opinionsOfSourceEntity
            }
         }
      }
      //The target is someone else. Determine my regard for that somebody else and scale response accordingly
      else {
         val sourceEntityId = intent.sourceEntity.id
         val opinionsOfSource = this.opinionsByEntityId[sourceEntityId] ?: Opinion()
         val regardOfSource = opinionsOfSource.byComponent.values.sum()

         val targetEntityId = effect.target.id
         val opinionsOfTarget = this.opinionsByEntityId[targetEntityId] ?: Opinion()
         val regardOfTarget = opinionsOfTarget.byComponent.values.sum()

         //"Regard" just leverages the fact that negative=bad and positive=good.
         //Positive number means they will agree with the action taken. Negative means disagree
         val regardDifference = regardOfSource - regardOfTarget

         /*TODO How should this work?
            Opinions and Mood should both change.
            Positive regard diff:
             - opinion of source should go up
             - opinion of target goes down
             -
            Negative regard diff = opinion of source should go down, up for target
             -
          */


      }

      log.info("Just processed social effect type $effect on target: $id")
   }
}