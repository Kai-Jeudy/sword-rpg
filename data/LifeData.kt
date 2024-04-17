package com.github.epickiller6002.mysticwoods.component

data class LifeData(
    var life: Float = 30f,
    var max: Float = 30f,
    var regeneration: Float = 0f,
    var takeDamage: Float = 0f,
) {
    val isDead: Boolean
        get() = life <= 0f
}