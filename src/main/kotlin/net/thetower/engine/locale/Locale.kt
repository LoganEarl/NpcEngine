package net.thetower.engine.locale

import kotlin.uuid.Uuid

interface Locale {
   val connectionsById: Map<Uuid, LocaleConnection>
}