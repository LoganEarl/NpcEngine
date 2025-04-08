package net.thetower.engine.state

import net.thetower.engine.entity.Entity
import net.thetower.engine.locale.LocaleModel

/**
 * This class is responsible for receiving
 */
class IntentProcessor(
    val localeModel: LocaleModel
) {
    private var nextIntents: MutableList<Intent> = mutableListOf()

    fun enqueueIntent(intent: Intent) {
        nextIntents.add(intent)
    }

    fun tick() {
        val intents: List<Intent> = nextIntents
        nextIntents = mutableListOf()

        val effects: MutableList<Effect> = mutableListOf()
        val notifications: MutableList<Notification> = mutableListOf()
        for (intent in intents) {
            val results = intent.execute()
            val notification = Notification(intent, effects)
            notifications.add(notification)
            effects.addAll(results)
        }

        for (effect in effects) {
            effect.apply()
        }

        for (notification in notifications) {
            val scope = notification.intent.scope
            val entitiesToNotify = mutableSetOf<Entity>()
            if (scope.visibleToAll)
                entitiesToNotify.addAll(localeModel.getAllActiveEntities())
            if(scope.visibleToSource)
                entitiesToNotify.add(notification.intent.sourceEntity)
            if(scope.visibleToTarget)
                entitiesToNotify.addAll(notification.effects.map {e -> e.target})
            if(scope.visibleToSelfLocale)
                entitiesToNotify.addAll(localeModel.getEntitiesNearby(notification.intent.sourceEntity))
            if(scope.visibleToTargetLocale)
                entitiesToNotify.addAll(notification.effects.map {e -> e.target}.flatMap { e -> localeModel.getEntitiesNearby(e)})
            entitiesToNotify.forEach { entity -> entity.notify(notification) }
        }
    }
}