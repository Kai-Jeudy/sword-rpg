package com.github.epickiller6002.mysticwoods.component

import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.github.epickiller6002.mysticwoods.ai.AiEntity
import com.github.epickiller6002.mysticwoods.ai.DefaultState
import com.github.epickiller6002.mysticwoods.ai.EntityState
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

data class StateData(
    var nextState: EntityState = DefaultState.IDLE,
    val stateMachine: DefaultStateMachine<AiEntity, EntityState> = DefaultStateMachine()
) {
    companion object {
        class StateComponentListener(
            private val world: World
        ) : ComponentListener<StateData> {
            override fun onComponentAdded(entity: Entity, component: StateData) {
                component.stateMachine.owner = AiEntity(entity, world)
            }

            override fun onComponentRemoved(entity: Entity, component: StateData) = Unit
        }
    }
}