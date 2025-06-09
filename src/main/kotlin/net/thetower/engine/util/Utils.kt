package net.thetower.engine.util

import kotlin.math.floor
import kotlin.math.pow
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties

fun Any.toStringByReflection(exclude: List<String> = listOf(), mask: List<String> = listOf()): String {
    val propsString = this::class.declaredMemberProperties
        .filter { exclude.isEmpty() || !exclude.contains(it.name) }
        .filter { prop -> prop.visibility == KVisibility.PUBLIC }
        .joinToString(", ") {
            val value = if (!mask.isEmpty() && mask.contains(it.name)) "****" else it.call(this).toString()
            "${it.name}=${value}"
        };

    return "${this::class.simpleName} [${propsString}]"
}

fun Double.withDecimalDigits(digits: Int): Double {
    val magnitude = 10.0.pow(digits.toDouble())
    return floor(this * magnitude) / magnitude
}