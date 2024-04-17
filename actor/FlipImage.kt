package com.github.epickiller6002.CAS_Project.actor

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable

class FlipImage : Image() {
    var flipX = false

    override fun draw(batch: Batch, parentAlpha: Float) {
        validate()
        val color = color
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        val toDraw = drawable

        val x = x
        val y = y
        val scaleX = scaleX
        val scaleY = scaleY
        if (toDraw is TransformDrawable && (scaleX != 1.0f || scaleY != 1.0f || rotation != 0.0f)) {
                toDraw.draw(
                    batch,
                    if(flipX) x + imageX + imageWidth * scaleX else x + imageX,
                    y + imageY,
                    originX - imageX,
                    originY - imageY,
                    imageWidth,
                    imageHeight,
                    if(flipX) -scaleX else scaleX,
                    scaleY,
                    rotation
                )
        }

        else{
            toDraw?.draw(
                batch,
                if(flipX) x + imageX + imageWidth * scaleX else x + imageX,
                y + imageY,
                if(flipX) -imageWidth * scaleX else imageWidth * scaleX,
                imageHeight * scaleY,
            )
        }
    }
}