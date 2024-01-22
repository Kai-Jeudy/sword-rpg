package com.github.epickiller6002.mysticwoods.ai

import com.badlogic.gdx.graphics.g2d.Animation
import com.github.epickiller6002.mysticwoods.component.AnimationType

enum class DefaultState: EntityState {
    IDLE {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.IDLE)
        }

        override fun update(entity: AiEntity) {
            when {
                entity.wantsToAttack -> entity.state(ATTACK)
                entity.wantsToRun -> entity.state(RUN)
            }
        }
    },

    RUN {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.RUN)
        }

        override fun update(entity: AiEntity) {
            when {
                entity.wantsToAttack -> entity.state(ATTACK)
                !entity.wantsToRun -> entity.state(IDLE)
            }
        }
    },
    ATTACK {
        override fun enter(entity: AiEntity) {
            entity.animation(AnimationType.ATTACK, Animation.PlayMode.NORMAL)
            entity.root()
            entity.startAttack()
        }
    },
    DEATH,
    RESURRECT,
}

enum class DefaultGlobalState: EntityState {
    CHECK_ALIVE,
}