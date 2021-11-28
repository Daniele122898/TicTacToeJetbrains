import react.*
import react.dom.*
import kotlinx.html.js.*
import kotlinx.coroutines.*

private val scope = MainScope()

val tictactoe = fc<Props> {
    val (game, setGame) = useState<Game>(Game("", ArrayList()))

    useEffectOnce {
        scope.launch {
            setGame(getCreateGame())
        }
    }

    fun getGridTypeString(item: GridItem): String {
        when (item.content) {
            GridType.Empty -> return ""
            GridType.Circle -> return "O"
            GridType.Cross -> return "X"
        }
    }

    fun getStateString(): String {
        when(game.state) {
            GameState.Draw -> return "Draw"
            GameState.AIWon -> return "AI Won"
            GameState.PlayerWon -> return "Player Won"
            GameState.Running -> return "Running"
        }
    }


    h1 {
        +"GameID: ${game.uuid}"
    }
    h1 {
        +"State: ${getStateString()}"
    }
    div("grid") {
        game.grid.forEach { rows ->
            rows.forEach { item ->
                div("grid_item") {
                    key = "${item.row},${item.col}"
                    attrs.onClickFunction = {
                        console.log("Clicked on ", item)
                        scope.launch {
                            postGameMove(game.uuid, Move(item.row, item.col, GridType.Cross))
                            // TODO separate into GameId and Grid because recreating this object everytime is not necessary
                            // Small move updates would also be great or a history but this is easier for now
                            setGame(getGame(game.uuid))
                        }
                    }
                    +getGridTypeString(item)
                }
            }
        }
    }

}