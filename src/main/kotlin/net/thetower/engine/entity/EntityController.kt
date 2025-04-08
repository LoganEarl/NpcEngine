package net.thetower.engine.entity

/**
 * EntityController implementations take charge of an entity. They interface with something
 * (a player, ai, etc) and decide what to do. Then they register intents on behalf of the entity.
 */
interface EntityController<T : Entity> {
    var entity: T
}
