import kotlinx.serialization.Serializable

@Serializable
data class Game(val uuid: String, val grid: ArrayList<GridItem>) {
    var playerTurn = true
}