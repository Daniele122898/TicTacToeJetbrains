import kotlinx.serialization.Serializable

@Serializable
enum class GridType {
    Empty,
    Cross,
    Circle,
}