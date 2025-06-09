package net.thetower.engine.state

import mu.KotlinLogging
import net.thetower.engine.entity.Entity
import net.thetower.engine.locale.LocaleModel
import kotlin.uuid.Uuid

/**
 * This class is responsible for receiving
 */
class IntentProcessor(
    val localeModel: LocaleModel,
) : Notification.Producer {
    companion object {
        val log = KotlinLogging.logger { }
    }

    override val subscribers: MutableMap<Uuid, Notification.Listener> = mutableMapOf()

    private var nextIntents: MutableList<Intent> = mutableListOf()
    private var tickNumber: Int = 0

    fun enqueueIntent(intent: Intent) {
        nextIntents.add(intent)
    }

    fun tick() {
        val intents: List<Intent> = nextIntents
        nextIntents = mutableListOf()
        tickNumber++
        log.info("Starting tick number: $tickNumber with ${intents.size} intents to process")

        val effects: MutableList<Effect> = mutableListOf()
        val notifications: MutableList<Notification> = mutableListOf()
        for (intent in intents) {
            val results = intent.execute()
            val notification = Notification(intent, results, tickNumber)
            notifications.add(notification)
            effects.addAll(results)
        }

        for (effect in effects) {
            effect.apply()
        }

        for (notification in notifications) {
            val scope = notification.intent.scope
            val entitiesToNotify = mutableSetOf<Entity>()
            if (scope.visibleToAll) entitiesToNotify.addAll(localeModel.getAllActiveEntities())
            if (scope.visibleToSource) entitiesToNotify.add(notification.intent.sourceEntity)
            if (scope.visibleToTarget) entitiesToNotify.addAll(notification.effects.map { it.target })
            if (scope.visibleToSelfLocale) entitiesToNotify.addAll(
                localeModel.getEntitiesNearby(
                    notification.intent.sourceEntity
                )
            )
            if (scope.visibleToTargetLocale) entitiesToNotify.addAll(notification.effects.map { it.target }
                .flatMap { e -> localeModel.getEntitiesNearby(e) })
            entitiesToNotify.forEach { entity -> entity.notify(notification) }
            //Our direct subscribers get all the notifications
            subscribers.values.forEach { it.notify(notification) }
        }

        localeModel.getAllActiveEntities().forEach { it.notifyOfTickEnd(tickNumber) }
        subscribers.values.forEach { it.notifyOfTickEnd(tickNumber) }
    }
}