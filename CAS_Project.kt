package com.github.epickiller6002.CAS_Project

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.github.epickiller6002.mysticwoods.screen.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen

class CAS_Project : KtxGame<KtxScreen>() {

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        addScreen(GameScreen())
        setScreen<GameScreen>()
    }

    companion object {
        const val  UNIT_SCALE = 1 / 16f
    }
}