package com.github.epickiller6002.mysticwoods.ai

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.github.epickiller6002.mysticwoods.component.*
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

data class AiEntity(
    private val entity: Entity,
    private val world: World,
    private val animationCmps: ComponentMapper<AnimationComponent> = world.mapper(),
    private val moveCmps: ComponentMapper<MoveData> = world.mapper(),
    private val attackCmps: ComponentMapper<AttackComponent> = world.mapper(),
    private val stateCmps: ComponentMapper<StateData> = world.mapper(),
    private val lifeCmps: ComponentMapper<LifeData> = world.mapper(),
    ) {
    val wantsToRun: Boolean
        get() {
            val moveCmp = moveCmps[entity]
            return moveCmp.cos != 0f || moveCmp.sin != 0f
        }

    val wantsToAttack: Boolean
        get() = attackCmps.getOrNull(entity)?.doAttack?: false

    val isAnimationDone: Boolean
        get() = animationCmps[entity].isAnimationDone

    val attackCmp: AttackComponent
        get() = attackCmps[entity]

    val isDead: Boolean
        get() = lifeCmps[entity].isDead

    fun animation(type: AnimationType, mode: PlayMode = PlayMode.LOOP, resetAnimation: Boolean = false) {
        with(animationCmps[entity]) {
            nextAnimation(type)
            playMode = mode
            if(resetAnimation) {
                stateTime = 0f
            }
        }
    }

    fun state(next: EntityState, immediateChange: Boolean = false) {
        with(stateCmps[entity]) {
            nextState = next
            if (immediateChange) {
                stateMachine.changeState(nextState)
            }
        }
    }

    fun enableGlobalState(enable: Boolean) {
        with(stateCmps[entity]) {
            if(enable) {
                stateMachine.globalState = DefaultGlobalState.CHECK_ALIVE
            } else {
                stateMachine.globalState = null
            }
        }
    }

    fun changeToPreviousState() {
        with(stateCmps[entity]) { nextState = stateMachine.previousState }
    }

    fun root(enable: Boolean) {
        with(moveCmps[entity]) { root = enable }
    }

    fun startAttack() {
        with(attackCmps[entity]) { startAttack() }
    }
}