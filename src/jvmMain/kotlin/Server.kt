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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

val collection = ArrayList<ShoppingListItem>()
val games = HashMap<String, Game>()

const val gridSize = 10*10;

fun main() {
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
                    val grid = ArrayList<GridItem>(gridSize)
                    // initialize grid
                    for (i in 1..gridSize) {
                        grid.add(GridItem(i-1))
                    }
                    val game = Game(uuid, grid)
                    games.put(uuid, game)
                    call.respond(game)
                }
                get("/{id}") {
                    val id = call.parameters["id"] ?: error("Invalid game id")
                    val game = games.get(id);
                    if (game == null)
                        error("Game not Found")

                    call.respond(game.grid)
                }
                post("/{id}") {
                    val id = call.parameters["id"] ?: error("Invalid game id")
                    val move = call.receive<Move>()
                    if (move.type == GridItemContent.Empty)
                        error("Cannot make an empty move")
                    // Valid game?
                    val game = games.get(id);
                    if (game == null)
                        error("Game not Found")

                    // Is player turn?
                    if (!game.playerTurn)
                        error("Not your turn!")

                    // valid move?
                    if (move.index < 0 || move.index >= gridSize)
                        error("Index out of grid bounds")
                    val pastItem = game.grid[move.index];
                    if (pastItem.content != GridItemContent.Empty)
                        error("Cannot make move on a field that is not empty")

                    // Move is valid. Make move and send back AI move
                    game.playerTurn = false
                    pastItem.content = move.type
                    call.respond(HttpStatusCode.OK)
                }
            }
//            route(ShoppingListItem.path) {
//                get {
//                    call.respond(collection)
//                }
//                post {
////                    collection.insertOne(call.receive<ShoppingListItem>())
//                    collection.add(call.receive<ShoppingListItem>())
//                    call.respond(HttpStatusCode.OK)
//                }
//                delete("/{id}") {
//                    val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
////                    collection.deleteOne(ShoppingListItem::id eq id)
//                    collection.removeIf(Predicate { t ->  t.id == id})
//                    call.respond(HttpStatusCode.OK)
//                }
//            }
        }
    }.start(wait = true)
}