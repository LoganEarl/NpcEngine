package net.thetower.implementation

import net.thetower.engine.entity.NpcEntity
import net.thetower.engine.locale.LocaleModel
import net.thetower.engine.npc.NpcController
import net.thetower.engine.npc.SocialInteractionModel
import net.thetower.engine.state.IntentProcessor
import net.thetower.implementation.intent.InsultIntent
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.direct
import org.kodein.di.instance
import kotlin.random.Random
import kotlin.uuid.Uuid


val di = DI {
    bindSingleton<LocaleModel> { SingleRoomModel() }
    bindSingleton<IntentProcessor> { IntentProcessor(instance()) }
    bindSingleton<Random> { Random.Default }
    bindSingleton<SocialInteractionModel> { SocialInteractionModel() }
}.direct

val npc1 = NpcEntity(Uuid.random(), di.instance())
val controller1 = NpcController(npc1, di)
val npc2 = NpcEntity(Uuid.random(), di.instance())

fun main() {
    val intentProcessor: IntentProcessor = di.instance()
    intentProcessor.enqueueIntent(InsultIntent(npc2, listOf(npc1), di))
    intentProcessor.tick()

}


