import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

val games = HashMap<String, Game>()
val access = HashMap<String, Long>()

const val gridDimension = 10;
const val winningScore = 5;
const val deleteOffsetMillis = 10*60*1000 // 10 minutes

fun gameCleanup() {
    val threshold = System.currentTimeMillis() - deleteOffsetMillis
    val iter = access.entries.iterator()
    while (iter.hasNext()) {
        val e = iter.next()
        if (e.value >= threshold)
            continue

        iter.remove()
        access.remove(e.key)
    }
}

fun main() {
    // Schedule game cleanup
    val timer = Timer("Games cleanup", true)
    timer.schedule(
        TimeUnit.MINUTES.toMillis(5),
        TimeUnit.MINUTES.toMillis(5)) {
        gameCleanup()
    }

    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }

        routing {
            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            static("/") {
                resources("")
            }
            route(GameRoute) {
                // Create Game room
                get {
                    val uuid = UUID.randomUUID().toString()
                    val grid = ArrayList<ArrayList<GridItem>>(gridDimension)
                    // initialize grid
                    for (i in 0..(gridDimension - 1)) {
                        grid.add(ArrayList<GridItem>(gridDimension))
                        for (j in 0..(gridDimension - 1)) {
                            grid[i].add(GridItem(i, j))
                        }
                    }
                    val game = Game(uuid, grid)
                    games.put(uuid, game)
                    access.put(uuid, System.currentTimeMillis())
                    call.respond(game)
                }
                get("/{id}") {
                    val id = call.parameters["id"] ?: error("Invalid game id")
                    val game = games.get(id);
                    if (game == null) {
                        call.respond(HttpStatusCode.BadRequest, "Game not Found")
                        return@get
                    }
                    call.respond(game)
                }
                post("/{id}") {
                    val id = call.parameters["id"] ?: error("Invalid game id")
                    val move = call.receive<Move>()
                    if (move.type == GridType.Empty) {
                        call.respond(HttpStatusCode.BadRequest, "Cannot make an empty move")
                        return@post
                    }
                    // Valid game?
                    val game = games.get(id);
                    if (game == null) {
                        call.respond(HttpStatusCode.BadRequest, "Game not Found")
                        return@post
                    }
                    if (game.state != GameState.Running) {
                        call.respond(HttpStatusCode.BadRequest, "Game already over")
                        return@post
                    }
                    // Is player turn?
                    if (!game.playerTurn) {
                        call.respond(HttpStatusCode.BadRequest, "Not your turn!")
                        return@post
                    }
                    // valid move?
                    if (move.row < 0 || move.row >= gridDimension ||
                        move.col < 0 || move.col >= gridDimension) {
                        call.respond(HttpStatusCode.BadRequest, "Index out of grid bounds")
                        return@post
                    }

                    val pastItem = game.grid[move.row][move.col];
                    if (pastItem.content != GridType.Empty) {
                        call.respond(HttpStatusCode.BadRequest, "Cannot make move on a field that is not empty")
                        return@post
                    }
                    // Move is valid. Make move and send back AI move
                    game.playerTurn = false
                    access.put(id, System.currentTimeMillis())
                    pastItem.content = move.type
                    if (CheckIfWinningMove(game.grid, pastItem)) {
                        game.state = GameState.PlayerWon
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }
                    val aiMove = AIMakeMove(game.grid, move)
                    // Ai couldnt make move so its definitely a draw since we didnt win above
                    if (aiMove == null) {
                        // Draw
                        game.state = GameState.Draw
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }
                    // First check if AI won before we check for a full grid again! otherwise we might give a draw if the last move was a winning move
                    if (CheckIfWinningMove(game.grid, game.grid[aiMove.row][aiMove.col]))
                        game.state = GameState.AIWon
                    else if (IsGridFull(game.grid))
                        game.state = GameState.Draw
                    else
                        game.playerTurn = true

                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }.start(wait = true)
}