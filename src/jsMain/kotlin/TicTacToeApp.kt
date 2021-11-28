import kotlinx.browser.localStorage
import react.*
import react.dom.*
import kotlinx.html.js.*
import kotlinx.coroutines.*
import kotlinx.html.classes
import org.w3c.dom.get
import react.dom.server.renderToStaticMarkup

private val scope = MainScope()

val tictactoe = fc<Props> {
    val (game, setGame) = useState<Game>(Game("", ArrayList()))
    val (score, setScore) = useState(Pair(0,0))

    useEffectOnce {
        val sc = localStorage.getItem("score")
        if (sc != null && sc.length > 0) {
            setScore(JSON.parse<Pair<Int, Int>>(sc))
        }
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

    if (game.state != GameState.Running) {
        div("modal") {
            div("modal_content") {
                h2("modal_header") {
                    +getStateString()
                }
                div("modal_button") {
                    +"Restart"

                    attrs.onClickFunction = {
                        scope.launch {
                            setGame(getCreateGame())
                        }
                    }
                }
            }
        }
    }

    h1("header") {
        +"Tic Tac Toe"
    }

    h1("score") {
        + "${score.first} - ${score.second}"
    }

    div("grid") {
        game.grid.forEach { rows ->
            rows.forEach { item ->
                div("grid_item") {
                    key = "${item.row},${item.col}"
                    if (item.content == GridType.Cross) {
                        attrs.classes = attrs.classes.plus("player")
                    } else if (item.content == GridType.Circle) {
                        attrs.classes = attrs.classes.plus("ai")
                    }
                    attrs.onClickFunction =  {
                        if (item.content == GridType.Empty) {
                            scope.launch {
                                postGameMove(game.uuid, Move(item.row, item.col, GridType.Cross))
                                // TODO separate into GameId and Grid because recreating this object everytime is not necessary
                                // Small move updates would also be great or a history but this is easier for now
                                val g = getGame(game.uuid)
                                setGame(g)

                                if (g.state == GameState.AIWon) {
                                    val s = Pair(score.first, score.second + 1)
                                    setScore(s)
                                    localStorage.setItem("score", JSON.stringify(s))
                                } else if (g.state == GameState.PlayerWon) {
                                    val s = Pair(score.first + 1, score.second)
                                    setScore(s)
                                    localStorage.setItem("score", JSON.stringify(s))
                                }
                            }
                        }
                    }
                    span {
                        +getGridTypeString(item)
                    }
                }
            }
        }
    }

}