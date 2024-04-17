package com.github.epickiller6002.mysticwoods.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.github.epickiller6002.mysticwoods.component.*
import com.github.quillraven.fleks.*
import ktx.assets.disposeSafely

@AllOf([LifeData::class])
@NoneOf([DeadData::class])
class LifeLogic(
    private val lifeCmps: ComponentMapper<LifeData>,
    private val deadCmps: ComponentMapper<DeadData>,
    private val playerCmps: ComponentMapper<PlayerData>,
    private val physicCmps: ComponentMapper<PhysicData>,
    private val aniCmps: ComponentMapper<AnimationComponent>,
    ): IteratingSystem() {
    private val damageFont = BitmapFont(Gdx.files.internal("damage.fnt"))
    private val floatingTextStyle = LabelStyle(damageFont, Color.WHITE)

    override fun onTickEntity(entity: Entity) {
        val lifeCmp = lifeCmps[entity]
        lifeCmp.life = (lifeCmp.life + lifeCmp.regeneration * deltaTime).coerceAtMost(lifeCmp.max)

        if(lifeCmp.takeDamage > 0) {
            val physicCmp = physicCmps[entity]
            lifeCmp.life -= lifeCmp.takeDamage
            floatingText(lifeCmp.takeDamage.toInt().toString(), physicCmp.body.position, physicCmp.size)
            lifeCmp.takeDamage = 0f
        }

        if(lifeCmp.isDead) {
            aniCmps.getOrNull(entity)?.let{aniCmp ->
                    aniCmp.nextAnimation(AnimationType.DEATH)
                    aniCmp.playMode = Animation.PlayMode.NORMAL
            }

            configureEntity(entity) {
                deadCmps.add(it) {
                    if(it in playerCmps) {
                        reviveTime = 7f
                    }
                }
            }
        }
    }



    private fun floatingText(text: String, position: Vector2, size: Vector2) {
        world.entity {
            add<FloatingTextData> {
                txtLocation.set(position.x, position.y - size.y * 0.5f)
                lifeSpan = 1.5f
                label = Label(text, floatingTextStyle)
            }
        }
    }

    override fun onDispose() {
        damageFont.disposeSafely()
    }

}