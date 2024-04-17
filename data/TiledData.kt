package com.github.epickiller6002.mysticwoods.component

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.github.quillraven.fleks.Entity


class TiledData {
    lateinit var cell: Cell
    val nearbyEntities = mutableSetOf<Entity>()
}