import kotlinx.serialization.Serializable

@Serializable
data class Move(val index: Int, val type: GridItemContent)
