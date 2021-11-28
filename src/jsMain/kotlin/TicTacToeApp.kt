import react.*
import react.dom.*
import kotlinx.html.js.*
import kotlinx.coroutines.*

private val scope = MainScope()

val tictactoe = fc<Props> {
    var gameId by useState("")

    useEffectOnce {
        scope.launch {
            gameId = getCreateGame()
        }
    }

    h1 {
        +"GameID: ${gameId}"
    }
}