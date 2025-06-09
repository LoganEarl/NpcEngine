package net.thetower.engine.state

import mu.KotlinLogging
import net.thetower.engine.entity.Entity
import net.thetower.engine.npc.SocialInteractionModel
import org.kodein.di.DirectDI
import org.kodein.di.instance
import kotlin.random.Random

/**
 * An additional layer of abstraction on top of intent. This adds conversations, a single sentiment, and which social
 * category a given statements falls under. Basically, everything needed to carry out a conversation.
 */
interface SocialIntent : Intent {
    val socialCategory: SocialCategory
    val sentiment: Sentiment
    val socialInteractionModel: SocialInteractionModel
}