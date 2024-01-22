package com.github.epickiller6002.mysticwoods.system

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.utils.Scaling
import com.github.epickiller6002.mysticwoods.MysticWoods.Companion.UNIT_SCALE
import com.github.epickiller6002.mysticwoods.actor.FlipImage
import com.github.epickiller6002.mysticwoods.component.*
import com.github.epickiller6002.mysticwoods.component.PhysicComponent.Companion.physicsCmpFromImage
import com.github.epickiller6002.mysticwoods.event.MapChangeEvent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.app.gdxError
import ktx.box2d.box
import ktx.math.vec2
import ktx.tiled.layer
import ktx.tiled.type
import ktx.tiled.x
import ktx.tiled.y
import kotlin.math.roundToInt

@AllOf([SpawnComponent::class])
class EntitySpawnSystem(
    private val phWorld: World,
    private val atlas: TextureAtlas,
    private val spawnCmps:ComponentMapper<SpawnComponent>,
) : EventListener, IteratingSystem(){
    private val cachedCfgs = mutableMapOf<String, SpawnCfg>()
    private val cachedSizes = mutableMapOf<AnimationModel, Vector2>()

    override fun onTickEntity(entity: Entity) {
        with(spawnCmps[entity]){
            val cfg = spawnCfg(type)
            val relativeSize = size(cfg.model)

            world.entity {
                val imageCmp = add<ImageComponent>{
                    image = FlipImage().apply {
                        setPosition(location.x, location.y)
                        setSize(relativeSize.x, relativeSize.y)
                        setScaling(Scaling.fill)
                    }
                }
                add<AnimationComponent> {
                    nextAnimation(cfg.model, AnimationType.IDLE)
                }

                physicsCmpFromImage(phWorld, imageCmp.image, cfg.bodyType) { phCmp, width, height ->
                    val w = width * cfg.physicScaling.x
                    val h = height * cfg.physicScaling.y
                    phCmp.offset.set(cfg.physicOffset)
                    phCmp.size.set(w, h)

                    // hit box
                    box(w, h, cfg.physicOffset) {
                        isSensor = cfg.bodyType != StaticBody
                        userData = HIT_BOX_SENSOR
                    }

                    if(cfg.bodyType != StaticBody) {
                        // collision box
                        val collH = h * 0.4f
                        val collOffset = vec2().apply { set(cfg.physicOffset) }
                        collOffset.y -= (h - collH) * 0.5f
                        box(w, collH, collOffset)
                    }
                }

                if(cfg.speedScaling > 0f) {
                    add<MoveComponent> {
                        speed = DEFAULT_SPEED * cfg.speedScaling
                    }
                }

                if(cfg.canAttack) {
                    add<AttackComponent> {
                        maxDelay = cfg.attackDelay
                        damage = (DEFAULT_ATTACK_DAMAGE * cfg.attackScaling).roundToInt()
                        extraRange = cfg.attackExtraRange
                    }
                }

                if(cfg.lifeScaling > 0f) {
                    add<LifeComponent> {
                        max = DEFAULT_LIFE * cfg.lifeScaling
                        life = max
                    }
                }

                if(type == "Player") {
                    add<PlayerComponent>()
                    add<StateComponent>()
                }

                if(cfg.lootable) {
                    add<LootComponent>()
                }

                if(cfg.bodyType != StaticBody) {
                    // such entities will create/remove collision objects
                    add<CollisionComponent>()
                }
            }
        }
        world.remove(entity)
    }

    private fun spawnCfg(type:String):SpawnCfg = cachedCfgs.getOrPut(type) {
        when (type) {
            "Player" -> SpawnCfg(
                AnimationModel.PLAYER,
                attackExtraRange = 0.6f,
                attackScaling = 1.25f,
                physicScaling = vec2(0.3f, 0.3f),
                physicOffset = vec2(0f, -10f * UNIT_SCALE),
            )
            "Slime" -> SpawnCfg(
                AnimationModel.SLIME,
                lifeScaling = 0.75f,
                physicScaling = vec2(0.3f, 0.3f),
                physicOffset = vec2(0f, -2f * UNIT_SCALE),
            )
            "Chest" -> SpawnCfg(
                AnimationModel.CHEST,
                speedScaling = 0f,
                bodyType = StaticBody,
                canAttack = false,
                lifeScaling = 0f,
                lootable = true,
            )
            else -> gdxError("Type $type has no SpawnCfg setup.")
        }
    }

    private fun size(model: AnimationModel) = cachedSizes.getOrPut(model) {
        val regions = atlas.findRegions("${model.atlasKey}/${AnimationType.IDLE.atlasKey}")
        println(model.atlasKey)
        println(regions)
        if(regions.isEmpty){
            gdxError("There are no regions for the idle animation of model $model")
        }
        val firstFrame = regions.first()
        vec2(firstFrame.originalWidth* UNIT_SCALE, firstFrame.originalHeight* UNIT_SCALE)
    }

    override fun handle(event: Event): Boolean {
        when(event){
            is MapChangeEvent -> {
                val entityLayer = event.map.layer("entities")
                entityLayer.objects.forEach { mapObj ->
                    val type = mapObj.type ?: gdxError("Map Object $mapObj does not have a type!")
                    world.entity {
                        add<SpawnComponent>{
                            this.type = type
                            this.location.set(mapObj.x*UNIT_SCALE, mapObj.y*UNIT_SCALE)
                        }
                    }
                }
                return true
            }
        }
        return false
    }

    companion object {
        const val HIT_BOX_SENSOR = "Hitbox"
    }
}