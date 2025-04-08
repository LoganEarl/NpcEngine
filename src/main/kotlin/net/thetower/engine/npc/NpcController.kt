package net.thetower.engine.npc

import net.thetower.engine.entity.EntityController
import net.thetower.engine.entity.NpcEntity
import net.thetower.engine.state.IntentProcessor
import net.thetower.engine.state.Notification
import net.thetower.implementation.intent.InsultIntent
import org.kodein.di.DirectDI
import org.kodein.di.instance
import kotlin.uuid.Uuid

class NpcController(
    override var entity: NpcEntity,
    val di: DirectDI
) : EntityController<NpcEntity>, Notification.Listener {
    init {
        entity.subscribe(this)
    }

    private val intentProcessor: IntentProcessor = di.instance()

    override val id = Uuid.random()

    override fun notify(notification: Notification) {
        //TODO eventually we will evaluate mental state and choose an action close to our state.
        // However, for now we are just going to be an asshole
        if (notification.intent.sourceEntity != this.entity) {
            intentProcessor.enqueueIntent(
                InsultIntent(
                    this.entity,
                    listOf(notification.intent.sourceEntity),
                    di.instance()
                )
            )
        }

    }
}

