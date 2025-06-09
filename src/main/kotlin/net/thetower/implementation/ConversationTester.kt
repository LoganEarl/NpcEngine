package net.thetower.implementation

import mu.KotlinLogging
import net.thetower.engine.entity.NpcEntity
import net.thetower.engine.npc.GenericNpcController
import net.thetower.engine.npc.Personality
import net.thetower.engine.npc.PersonalityComponent
import net.thetower.engine.npc.SocialInteractionModel
import net.thetower.engine.state.*
import net.thetower.engine.util.withDecimalDigits
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.direct
import org.kodein.di.instance
import kotlin.random.Random
import kotlin.uuid.Uuid

val testPersonalities = mapOf<Personality, Int>(
    Personality(mutableMapOf(PersonalityComponent.CONTENTIOUS_EXTOLLING to -.9)) to 1,
//    Personality(mutableMapOf(PersonalityComponent.CONTENTIOUS_EXTOLLING to -.5)) to 2,
//    Personality(mutableMapOf(PersonalityComponent.CONTENTIOUS_EXTOLLING to -.25)) to 5,
//    Personality(mutableMapOf(PersonalityComponent.CONTENTIOUS_EXTOLLING to 0.0)) to 10,
//    Personality(mutableMapOf(PersonalityComponent.CONTENTIOUS_EXTOLLING to 0.25)) to 5,
//    Personality(mutableMapOf(PersonalityComponent.CONTENTIOUS_EXTOLLING to 0.5)) to 2,
    Personality(mutableMapOf(PersonalityComponent.CONTENTIOUS_EXTOLLING to 0.9)) to 1,
)

val npcs = testPersonalities.entries
    .flatMap { entry -> List(entry.value) { entry.key } }
    .map { NpcEntity(Uuid.random(), it) }

val di = DI {
    bindSingleton<ConversationRegistry> { ConversationRegistry(instance()) }
    bindSingleton<ConversationalRoomModel> { ConversationalRoomModel(npcs.toSet()) }
    bindSingleton<IntentProcessor> { IntentProcessor(instance()) }
    bindSingleton<Random> { Random.Default }
    bindSingleton<Set<Sentiment>> { Sentiments.entries.toSet() }
    bindSingleton<Set<SocialCategory>> { SocialCategories.entries.toSet() }
    bindSingleton<SocialInteractionModel> { SocialInteractionModel(instance(), instance(), instance()) }
}.direct

val log = KotlinLogging.logger {}

fun main() {
    npcs.map { GenericNpcController(it, di.instance(), di.instance(), di.instance(), di.instance()) }
    di.instance<ConversationalRoomModel>().conversationRegistry = di.instance() //A hacky hack. Lazy DI would be better
    val conversationPermutations = mutableListOf<Pair<NpcEntity, NpcEntity>>()
    for (repeatCount in 1..20) {
        for (i in 0..(npcs.count() - 2)) {
            for (j in (i + 1)..(npcs.count() - 1)) {
                //Have each take turns starting to talk once
                conversationPermutations.add(Pair(npcs[i], npcs[j]))
                conversationPermutations.add(Pair(npcs[j], npcs[i]))
            }
        }
    }

    val sentimentCountsByNpcId = mutableMapOf<Uuid, MutableMap<Sentiment, Int>>()
    di.instance<IntentProcessor>().subscribers.put(Uuid.random(), object : Notification.Listener {
        override val id: Uuid = Uuid.random()
        override fun notify(notification: Notification) {
            val count = sentimentCountsByNpcId.computeIfAbsent(notification.intent.sourceEntity.id) { mutableMapOf() }
            if (notification.intent is GenericSocialIntent) {
                count.put(notification.intent.sentiment, count.getOrDefault(notification.intent.sentiment, 0) + 1)
            }
        }
    })

    val currentPermutations = mutableListOf<Pair<NpcEntity, NpcEntity>>()
    val reservedEntityIds = mutableSetOf<Uuid>()
    while (conversationPermutations.isNotEmpty()) {
        val nextPermutation = conversationPermutations.firstOrNull {
            !reservedEntityIds.contains(it.first.id) && !reservedEntityIds.contains(it.second.id)
        }
        if (nextPermutation == null) {
            simulatePermutations(currentPermutations)
            currentPermutations.clear()
            reservedEntityIds.clear()
        } else {
            reservedEntityIds.add(nextPermutation.first.id)
            reservedEntityIds.add(nextPermutation.second.id)
            currentPermutations.add(nextPermutation)
            conversationPermutations.remove(nextPermutation)
        }
    }

    summarizeNpcStates(npcs, sentimentCountsByNpcId)
}


fun summarizeNpcStates(npcs: List<NpcEntity>, sentimentCountsByNpc: Map<Uuid, Map<Sentiment, Int>>) {
    for ((index, npc) in npcs.withIndex()) {
        val opinionsOfOthers = npc.opinionsByEntityId.values
            .flatMap { it.components.values }
            .map { it.withDecimalDigits(3) }
        val averageOpinionOfOthers = (opinionsOfOthers.sum() / opinionsOfOthers.size).withDecimalDigits(3)

        val othersOpinionsOfNpc = npcs
            .filter { it != npc }
            .flatMap { it.opinionsByEntityId[npc.id]?.components?.values ?: emptyList() }
            .map { it.withDecimalDigits(3) }
        val averageOpinionOfNpc = (othersOpinionsOfNpc.sum() / opinionsOfOthers.size).withDecimalDigits(3)

        val sentimentCount = sentimentCountsByNpc[npc.id]
        val message = """NPC #$index with ID:${npc.id}
            Mood: ${npc.mood.components}
            NpcOpinionsOfOthers: $averageOpinionOfOthers $opinionsOfOthers
            OthersOpinionOfNpc: $averageOpinionOfNpc $othersOpinionsOfNpc
            SentimentsExpressed: $sentimentCount
        """.trimIndent()
        log.info(message)
    }
}

fun determineStartingSentiment(
    initiator: NpcEntity,
    receiver: NpcEntity,
    socialInteractionModel: SocialInteractionModel,
    random: Random
): Sentiment {
    var possibleSentiments = socialInteractionModel.loadedSentiments
    var currentMentalState = initiator.getMentalStateTowards(receiver.id)
    return possibleSentiments.minBy {
        it.mentalStateAffinities.euclideanDistanceTo(currentMentalState, socialInteractionModel.fuzzingFactor, random)
    }
}

fun simulatePermutations(permutations: List<Pair<NpcEntity, NpcEntity>>) {
    log.info("Starting a new batch of permutations using ${permutations.size} permutations")
    val intentProcessor: IntentProcessor = di.instance()
    val conversationRegistry: ConversationRegistry = di.instance()
    val socialInteractionModel: SocialInteractionModel = di.instance()
    val random: Random = di.instance()

    for (permutation in permutations) {
        conversationRegistry.startConversation(permutation.first, permutation.second)
        val sentiment = determineStartingSentiment(
            permutation.first, permutation.second, socialInteractionModel, random
        )
        intentProcessor.enqueueIntent(
            GenericSocialIntent(
                SocialCategories.GREETING,
                sentiment,
                permutation.first,
                listOf(permutation.second),
                NotificationScope.SUBJECT_LOCALES,
                setOf(NotificationCategory.SOCIAL),
                socialInteractionModel,
                conversationRegistry
            )
        )
    }

    while (conversationRegistry.hasActiveConversations) {
        intentProcessor.tick()
    }
}


