package com.github.epickiller6002.mysticwoods.system

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.epickiller6002.mysticwoods.component.FloatingTextData
import com.github.quillraven.fleks.*
import ktx.math.vec2

@AllOf([FloatingTextData::class])
class FloatingTextLogic(
    private val gameStage: Stage,
    @Qualifier("uiStage") private val uiStage: Stage,
    private val textCmps: ComponentMapper<FloatingTextData>,
    ): IteratingSystem() {
    private val uiLocation = vec2()
    private val uiTarget = vec2()

    private fun Vector2.toUiCoordinates(from: Vector2) {
        this.set(from)
        gameStage.viewport.project(this)
        uiStage.viewport.unproject(this)
    }

    override fun onTickEntity(entity: Entity) {
        with(textCmps[entity]) {
            if(time >= lifeSpan) {
                world.remove(entity)
                return
            }

            time += deltaTime

            uiLocation.toUiCoordinates(txtLocation)
            uiTarget.toUiCoordinates(txtTarget)
            uiLocation.interpolate(uiTarget, (time/lifeSpan).coerceAtMost(0.5f), Interpolation.smooth)
            label.setPosition(uiLocation.x, uiStage.viewport.worldHeight - uiLocation.y)
        }
    }

}