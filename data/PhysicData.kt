package com.github.epickiller6002.mysticwoods.component

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.epickiller6002.CAS_Project.CAS_Project.Companion.UNIT_SCALE
import com.github.epickiller6002.mysticwoods.system.CollisionSpawnLogic.Companion.SPAWN_AREA_SIZE
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateCfg
import ktx.app.gdxError
import ktx.box2d.BodyDefinition
import ktx.box2d.body
import ktx.box2d.circle
import ktx.box2d.loop
import ktx.math.vec2

class PhysicData {

    val prevPos = vec2()
    val impulse = vec2()
    val offset = vec2()
    val size = vec2()
    lateinit var body : Body

    companion object {
        fun EntityCreateCfg.physicsCmpFromShape2D(
            world: World,
            x: Int,
            y: Int,
            shape: Shape2D
        ) :PhysicData {
            when(shape) {
                is Rectangle -> {
                    val bodyX = x + shape.x * UNIT_SCALE
                    val bodyY = y + shape.y * UNIT_SCALE
                    val bodyW = shape.width * UNIT_SCALE
                    val bodyH = shape.height * UNIT_SCALE

                    return add{
                        body = world.body(BodyType.StaticBody) {
                            position.set(bodyX, bodyY)
                            fixedRotation = true
                            allowSleep = false
                            loop(
                                vec2(0f, 0f),
                                vec2(bodyW, 0f),
                                vec2(bodyW, bodyH),
                                vec2(0f, bodyH)
                            )
                            circle(SPAWN_AREA_SIZE*2f){ isSensor = true }
                        }
                    }
                }
                else -> gdxError("Shape $shape is not supported!")
            }
        }

        fun EntityCreateCfg.physicsCmpFromImage(
            world: World,
            image: Image,
            bodyType: BodyType,
            fixtureAction:BodyDefinition.(PhysicData, Float, Float) -> Unit
        ) :PhysicData {
            val x = image.x
            val y = image.y
            val w = image.width
            val h = image.height

            return add {
                body = world.body(bodyType) {
                    position.set(x+w*0.5f, y+h*0.5f)
                    fixedRotation = true
                    allowSleep = false
                    this.fixtureAction(this@add, w, h)
                }
            }
        }

        class PhysicComponentListener : ComponentListener<PhysicData> {
            override fun onComponentAdded(entity: Entity, component: PhysicData) {
                component.body.userData = entity
            }

            override fun onComponentRemoved(entity: Entity, component: PhysicData) {
                val body = component.body
                component.body.world.destroyBody(component.body)
                body.userData = null
            }
        }
    }
}