package com.github.epickiller6002.mysticwoods.system

import com.github.epickiller6002.mysticwoods.component.StateData
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([StateData::class])
class StateLogic(
    private val stateCmps: ComponentMapper<StateData>,
): IteratingSystem() {
    override fun onTickEntity(entity: Entity) {
        with(stateCmps[entity]) {
            if(nextState != stateMachine.currentState) {
                stateMachine.changeState(nextState)
            }
            stateMachine.update()
        }
    }

}