package net.thetower.engine.state

import kotlin.uuid.Uuid

class Notification(
    val intent: Intent,
    val effects: List<Effect>
) {
    interface Producer {
        val subscribers: MutableMap<Uuid, Listener>
        fun subscribe(listener: Listener) {
            subscribers[listener.id] = listener
        }
        fun unsubscribe(uuid: Uuid) {
            subscribers.remove(uuid)
        }
    }
    interface Listener {
        val id: Uuid
        fun notify(notification: Notification)
    }


}

