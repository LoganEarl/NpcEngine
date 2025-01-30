package di

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import state.GameEngine

class Global {
    companion object {
        val di = DI {
            bindSingleton<GameEngine> { GameEngine() }
        }
    }
}