package com.github.epickiller6002.mysticwoods.component

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Qualifier
import ktx.actors.plusAssign
import ktx.math.vec2

class FloatingTextData {
    val txtLocation = vec2()
    var txtTarget = vec2()
    var lifeSpan = 0f
    var time = 0f
    lateinit var  label: Label

    companion object {
        class FloatingTextComponentListener(
            @Qualifier("uiStage") private val uiStage: Stage,
        ): ComponentListener<FloatingTextData> {
            override fun onComponentAdded(entity: Entity, component: FloatingTextData) {
                uiStage.addActor(component.label)
                component.label += fadeOut(component.lifeSpan, Interpolation.pow3OutInverse)
                component.txtTarget.set(
                    component.txtLocation.x + MathUtils.random(-1.5f, 1.5f),
                    component.txtLocation.y + 1f
                )
            }

            override fun onComponentRemoved(entity: Entity, component: FloatingTextData) {
                uiStage.root.removeActor(component.label)
            }

        }
    }
}