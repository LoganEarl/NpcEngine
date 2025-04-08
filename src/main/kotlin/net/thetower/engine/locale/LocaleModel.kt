package net.thetower.engine.locale

import net.thetower.engine.entity.Entity
import kotlin.uuid.Uuid

interface LocaleModel {
    val localesById: Map<Uuid, Locale>

    fun getEntitiesNearby(entity: Entity): Set<Entity>
    fun getAllActiveEntities(): Set<Entity>
}