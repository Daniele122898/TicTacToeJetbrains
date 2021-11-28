import kotlinx.serialization.Serializable

@Serializable
data class Game(val uuid: String, val grid: ArrayList<ArrayList<GridItem>>) {
    var playerTurn = true
    var state = GameState.Running
}