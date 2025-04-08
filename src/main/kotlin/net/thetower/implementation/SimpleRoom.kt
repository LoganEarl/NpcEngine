package net.thetower.implementation

import net.thetower.engine.locale.Locale
import net.thetower.engine.locale.LocaleConnection
import kotlin.uuid.Uuid

class SimpleRoom: Locale {
    override val connectionsById: Map<Uuid, LocaleConnection> = emptyMap()
}