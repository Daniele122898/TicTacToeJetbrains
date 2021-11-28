import kotlin.math.max
import kotlin.math.min

fun AIMakeMove(grid: ArrayList<ArrayList<GridItem>>, playerMove: Move): Pair<Move, Int>? {
    val empty = ArrayList<GridItem>()
    grid.forEach { row ->
        row.forEach { item ->
            if (item.content == GridType.Empty)
                empty.add(item)
        }
    }

    if (empty.size == 0)
        return null

    val ranked = empty
        .map { item -> Pair(item, GetMoveScore(grid, item, playerMove)) }
        .sortedByDescending { pair -> pair.second }

    val move = ranked[0].first
    grid[move.row][move.col].content = GridType.Circle
    return Pair(Move(move.row, move.col, GridType.Circle), ranked[0].second)

//    var visited = ArrayList<ArrayList<Boolean>>(gridDimension)
//    for ((i, row) in grid.withIndex()) {
//        visited.add(ArrayList<Boolean>(gridDimension))
//        for ((j, item) in row.withIndex()) {
//            if (visited[i][j])
//                continue
//            visited[i][j]
//            // walk to the right
//            // walk down
//            // walk both diagonals
//        }
//    }
}

fun CheckIfWinningMove(grid: ArrayList<ArrayList<GridItem>>, move: GridItem): Boolean {
    return GetMoveScore(grid, move, null) >= 100
}

fun GetMoveScore(grid: ArrayList<ArrayList<GridItem>>, item: GridItem, playerMove: Move?): Int {
    var score = 0
    // Check horizontal
    score = max(score, CountHorizontal(grid, item, GridType.Circle))
    score = max(score, CountHorizontal(grid, item, GridType.Cross))
    // Check Vertical
    score = max(score, CountVertical(grid, item, GridType.Circle))
    score = max(score, CountVertical(grid, item, GridType.Cross))
    // Check Diagonal
    score = max(score, CountDiagRight(grid, item, GridType.Cross))
    score = max(score, CountDiagRight(grid, item, GridType.Circle))
    score = max(score, CountDiagLeft(grid, item, GridType.Cross))
    score = max(score, CountDiagLeft(grid, item, GridType.Circle))

    return score
}

fun CountHorizontal(grid: ArrayList<ArrayList<GridItem>>, item: GridItem, type: GridType): Int {
    var score = 0
    // Walk right
    for (i in item.col+1..gridDimension-1) {
        if (grid[item.row][i].content != type)
            break
        ++score
    }
    // Walk left
    for (i in item.col-1 downTo 0) {
        if (grid[item.row][i].content != type)
            break
        ++score
    }

    // Make sure we dont block ourselfs
    val dis = min(item.col, gridDimension-item.col) +1
    if (dis < winningScore - score && type == GridType.Circle)
        return 0

    // If this move could immediately win / loose, we need to prioritize!
    if (score == winningScore - 1)
        return 100

    return score
}

fun CountDiagRight(grid: ArrayList<ArrayList<GridItem>>, item: GridItem, type: GridType): Int {
    var score = 0

    // Walk up right
    var i = 1
    while (item.col+i < gridDimension && item.row-i > 0) {
        val row = item.row -i
        val col = item.col +i
        if (grid[row][col].content != type)
            break

        ++score
        ++i
    }
    i = 1
    // walk down left
    while (item.col-i > 0 && item.row+i < gridDimension) {
        val row = item.row +i
        val col = item.col -i
        if (grid[row][col].content != type)
            break

        ++score
        ++i
    }

    // If this move could immediately win / loose, we need to prioritize!
    if (score == winningScore - 1)
        return 100

    return score
}

fun CountDiagLeft(grid: ArrayList<ArrayList<GridItem>>, item: GridItem, type: GridType): Int {
    var score = 0

    // Walk up right
    var i = 1
    while (item.col-i > 0 && item.row-i > 0) {
        val row = item.row -i
        val col = item.col -i
        if (grid[row][col].content != type)
            break

        ++score
        ++i
    }
    i = 1
    // walk down left
    while (item.col+i < gridDimension && item.row+i < gridDimension) {
        val row = item.row +i
        val col = item.col +i
        if (grid[row][col].content != type)
            break

        ++score
        ++i
    }

    // If this move could immediately win / loose, we need to prioritize!
    if (score == winningScore - 1)
        return 100

    return score
}

fun CountVertical(grid: ArrayList<ArrayList<GridItem>>, item: GridItem, type: GridType): Int {
    var score = 0
    // Walk down
    for (i in item.row+1..gridDimension-1) {
        if (grid[i][item.col].content != type)
            break
        ++score
    }
    // Walk up
    for (i in item.row-1 downTo 0) {
        if (grid[i][item.col].content != type)
            break
        ++score
    }

    // Make sure we dont block ourselfs
    val dis = min(item.row, gridDimension-item.row) +1
    if (dis < winningScore - score && type == GridType.Circle)
        return 0

    // If this move could immediately win / loose, we need to prioritize!
    if (score == winningScore - 1)
        return 100

    return score
}