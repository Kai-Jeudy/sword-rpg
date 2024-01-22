package com.github.epickiller6002.mysticwoods.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.World
import com.github.epickiller6002.mysticwoods.component.*
import com.github.epickiller6002.mysticwoods.system.EntitySpawnSystem.Companion.HIT_BOX_SENSOR
import com.github.quillraven.fleks.*
import ktx.box2d.query
import ktx.math.component1
import ktx.math.component2

@AllOf([AttackComponent::class, PhysicComponent::class, ImageComponent::class])
class AttackSystem(
    //good idea to split up the components
    private val attackCmps: ComponentMapper<AttackComponent>,
    private val physicCmps: ComponentMapper<PhysicComponent>,
    private val imgCmps: ComponentMapper<ImageComponent>,
    private val lifeCmps: ComponentMapper<LifeComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>,
    private val lootCmps: ComponentMapper<LootComponent>,
    private val phWorld: World,
): IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val attackCmp = attackCmps[entity]

        if(attackCmp.isReady && !attackCmp.doAttack) {
            // entity does not want to attack and is not executing an attack -> do nothing
            return
        }

        if(attackCmp.isPrepared && attackCmp.doAttack) {
            // attack intention and is ready to attack -> start attack
            attackCmp.doAttack = false
            attackCmp.state = AttackState.ATTACKING
            attackCmp.delay = attackCmp.maxDelay
            return
        }

        attackCmp.delay -= deltaTime
        if(attackCmp.delay <= 0f && attackCmp.isAttacking) {
            // deal damage to nearby enemies
            attackCmp.state = AttackState.DEAL_DAMAGE

            val image = imgCmps[entity].image
            val physicCmp = physicCmps[entity]
            val attackLeft = image.flipX
            val (x, y) = physicCmp.body.position
            val(offX, offY) = physicCmp.offset
            val(w, h) = physicCmp.size
            val halfW = w * 0.5f
            val halfH = h * 0.5f

            if(attackLeft) {
                AABB_RECT.set(
                    x + offX - halfW - attackCmp.extraRange,
                    y + offY - halfH,
                    x + offX + halfW,
                    y + offY + halfH
                )
            } else {
                AABB_RECT.set(
                    x + offX - halfW,
                    y + offY - halfH,
                    x + offX + halfW + attackCmp.extraRange,
                    y + offY + halfH
                )
            }

            phWorld.query(AABB_RECT.x, AABB_RECT.y, AABB_RECT.width, AABB_RECT.height) { fixture ->
                if(fixture.userData != HIT_BOX_SENSOR) {
                    return@query true
                }

                val fixtureEntity = fixture.entity
                // fix entities can fight others (solution could be that if they have the same component they won't deal damage)
                if(fixtureEntity == entity) {
                    // prevent self-attack
                    return@query true
                }

                configureEntity(fixtureEntity) {
                    lifeCmps.getOrNull(it)?.let { lifeCmp ->
                        lifeCmp.takeDamage += attackCmp.damage * MathUtils.random(0.9f, 1.1f)
                    }

                    if(entity in playerCmps) {
                        lootCmps.getOrNull(it)?.let { lootCmp ->
                            lootCmp.interactEntity = entity
                        }
                    }
                }

                return@query true
            }

            attackCmp.state = AttackState.READY
        }

        }

        companion object {
            val AABB_RECT = Rectangle()

    }

}