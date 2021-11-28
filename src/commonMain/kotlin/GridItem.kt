import kotlinx.serialization.Serializable

@Serializable
data class GridItem(val index: Int) {
    val content: GridItemContent = GridItemContent.Empty
}
