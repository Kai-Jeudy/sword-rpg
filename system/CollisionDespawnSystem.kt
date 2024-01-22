package com.github.epickiller6002.mysticwoods.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.epickiller6002.mysticwoods.component.TiledComponent
import com.github.epickiller6002.mysticwoods.event.CollisionDespawnEvent
import com.github.epickiller6002.mysticwoods.event.fire
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([TiledComponent::class])
class CollisionDespawnSystem(
    private val tiledCmps: ComponentMapper<TiledComponent>,
    private val stage: Stage
): IteratingSystem(){
    override fun onTickEntity(entity: Entity) {
        with(tiledCmps[entity]) {
            if (nearbyEntities.isEmpty()) {
                stage.fire(CollisionDespawnEvent(cell))
                world.remove(entity)
            }
        }
    }
}