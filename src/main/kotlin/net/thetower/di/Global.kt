package net.thetower.di

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import net.thetower.state.GameEngine

class Global {
    companion object {
        val di = DI {
            bindSingleton<GameEngine> { GameEngine() }
        }
    }
}