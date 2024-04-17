package com.github.epickiller6002.mysticwoods.system

import com.github.epickiller6002.mysticwoods.ai.DefaultState
import com.github.epickiller6002.mysticwoods.component.DeadData
import com.github.epickiller6002.mysticwoods.component.LifeData
import com.github.epickiller6002.mysticwoods.component.StateData
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([DeadData::class])
class DeadLogic(
    private val deadCmps: ComponentMapper<DeadData>,
    private val lifeCmps: ComponentMapper<LifeData>,
    private val stateCmps: ComponentMapper<StateData>,
    ): IteratingSystem(){

    override fun onTickEntity(entity: Entity) {
        val deadCmp = deadCmps[entity]
        if(deadCmp.reviveTime == 0f) {
            world.remove(entity)
            return
        }

        deadCmp.reviveTime -= deltaTime
        if(deadCmp.reviveTime <= 0f) {
            with(lifeCmps[entity]){life = max}
            stateCmps.getOrNull(entity)?.let { stateCmp ->
                stateCmp.nextState= DefaultState.RESURRECT
            }
            configureEntity(entity){deadCmps.remove(entity)}
        }
    }

}