import kotlinx.serialization.Serializable

@Serializable
data class GridItem(val index: Int) {
    var content: GridItemContent = GridItemContent.Empty
}
