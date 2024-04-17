package com.github.epickiller6002.mysticwoods.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.epickiller6002.mysticwoods.component.TiledData
import com.github.epickiller6002.mysticwoods.event.CollisionDespawnEvent
import com.github.epickiller6002.mysticwoods.event.fire
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([TiledData::class])
class CollisionDespawnLogic(
    private val tiledCmps: ComponentMapper<TiledData>,
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