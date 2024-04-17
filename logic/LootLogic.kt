package com.github.epickiller6002.mysticwoods.system

import com.badlogic.gdx.graphics.g2d.Animation
import com.github.epickiller6002.mysticwoods.component.AnimationComponent
import com.github.epickiller6002.mysticwoods.component.AnimationType
import com.github.epickiller6002.mysticwoods.component.LootData
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([LootData::class])
class LootLogic(
    private val lootCmps: ComponentMapper<LootData>,
    private val aniCmps: ComponentMapper<AnimationComponent>,
): IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        with(lootCmps[entity]) {
            if(interactEntity == null) {
                return
            }

            configureEntity(entity) { lootCmps.remove(it) }
            aniCmps.getOrNull(entity)?.let { aniCmp ->
                aniCmp.nextAnimation(AnimationType.OPEN)
                aniCmp.playMode = Animation.PlayMode.NORMAL
            }
        }
    }

}