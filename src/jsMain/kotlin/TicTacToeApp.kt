import csstype.*
import react.*
import react.dom.*
import kotlinx.html.js.*
import kotlinx.coroutines.*
import kotlinx.html.style

private val scope = MainScope()

val tictactoe = fc<Props> {
    val (game, setGame) = useState<Game>(Game("", ArrayList()))

    useEffectOnce {
        scope.launch {
            setGame(getCreateGame())
        }
    }

    fun getGridContent(item: GridItem): String {
        when (item.content) {
            GridItemContent.Empty -> return ""
            GridItemContent.Circle -> return "O"
            GridItemContent.Cross -> return "X"
        }
    }


    h1 {
        +"GameID: ${game.uuid}"
    }
    div("grid") {
        game.grid.forEach { item ->
            div("grid_item") {
                key= item.index.toString()
                attrs.onClickFunction = {
                    console.log("Clicked on ", item)
                }
                +getGridContent(item)
            }
        }
    }

}