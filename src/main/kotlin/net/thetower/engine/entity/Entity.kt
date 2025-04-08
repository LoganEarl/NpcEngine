package net.thetower.engine.entity

import net.thetower.engine.state.Notification

/**
 * Entities are basically people, monsters, etc. in the world.
 * When it changes it emits notifications to subscribers.
 */
interface Entity : Notification.Producer, Notification.Listener {

}