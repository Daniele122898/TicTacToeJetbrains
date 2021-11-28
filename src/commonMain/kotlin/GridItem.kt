import kotlinx.serialization.Serializable

@Serializable
data class GridItem(val row: Int, val col: Int) {
    var content: GridType = GridType.Empty
}
