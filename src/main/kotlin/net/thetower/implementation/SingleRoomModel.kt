package net.thetower.implementation

import net.thetower.engine.entity.Entity
import net.thetower.engine.locale.Locale
import net.thetower.engine.locale.LocaleModel
import kotlin.uuid.Uuid

class SingleRoomModel: LocaleModel {
    private val singleRoomId = Uuid.random()
    override val localesById: Map<Uuid, Locale> = mapOf(
        Pair(singleRoomId, SimpleRoom())
    )
    private val entities: MutableMap<Uuid, Entity> = mutableMapOf()

    override fun getEntitiesNearby(entity: Entity): Set<Entity> {
       return entities.values.toSet()
    }

    override fun getAllActiveEntities(): Set<Entity> {
        return entities.values.toSet()
    }
}