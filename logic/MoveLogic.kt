package com.github.epickiller6002.mysticwoods.system

import com.github.epickiller6002.mysticwoods.component.ImageData
import com.github.epickiller6002.mysticwoods.component.MoveData
import com.github.epickiller6002.mysticwoods.component.PhysicData
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.math.component1
import ktx.math.component2

@AllOf([MoveData::class, PhysicData::class])
class MoveLogic(
    private val moveCmps: ComponentMapper<MoveData>,
    private val physicCmps: ComponentMapper<PhysicData>,
    private val imageCmps: ComponentMapper<ImageData>,
): IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val moveCmp = moveCmps[entity]
        val physicCmp = physicCmps[entity]
        val mass = physicCmp.body.mass
        val (velX, velY) = physicCmp.body.linearVelocity

        if((moveCmp.cos == 0f && moveCmp.sin == 0f ) || moveCmp.root){
            // no direction specified or rooted -> stop entity immediately
            physicCmp.impulse.set(
                mass * (0f - velX),
                mass * (0f - velY)
            )
            return
        }

        physicCmp.impulse.set(
            mass * (moveCmp.speed * moveCmp.cos - velX),
            mass * (moveCmp.speed * moveCmp.sin - velY)
        )
        
        imageCmps.getOrNull(entity)?.let { imageCmp ->
            if(moveCmp.cos != 0f) {
                imageCmp.image.flipX = moveCmp.cos < 0
            }
        }
    }
}