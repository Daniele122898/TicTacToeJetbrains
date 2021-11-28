import kotlinx.serialization.Serializable

@Serializable
enum class GameState {
    Running,
    AIWon,
    PlayerWon,
    Draw
}