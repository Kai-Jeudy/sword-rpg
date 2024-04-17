package com.github.epickiller6002.mysticwoods.component

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.epickiller6002.CAS_Project.actor.FlipImage
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity

class ImageData:Comparable<ImageData>{
    lateinit var image: FlipImage

    override fun compareTo(other: ImageData): Int {
        val yDiff = other.image.y.compareTo(image.y)
        return if(yDiff != 0) {
            yDiff
        }
        else {
            other.image.x.compareTo(image.x)
        }
    }

    companion object {
        class ImageComponentListener(private val stage:Stage) : ComponentListener<ImageData> {
            override fun onComponentAdded(entity: Entity, component: ImageData) {
                stage.addActor(component.image)
            }

            override fun onComponentRemoved(entity: Entity, component: ImageData) {
                stage.root.removeActor(component.image)
            }
        }
    }
}