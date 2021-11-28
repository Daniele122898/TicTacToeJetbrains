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
import java.util.function.Predicate
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

val collection = ArrayList<ShoppingListItem>()
val games = HashMap<String, ArrayList<GridItem>>()

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
                    val grid = ArrayList<GridItem>(10*10)
                    games.put(uuid, grid)
                    call.respond(uuid)
                }
            }
            route(ShoppingListItem.path) {
                get {
                    call.respond(collection)
                }
                post {
//                    collection.insertOne(call.receive<ShoppingListItem>())
                    collection.add(call.receive<ShoppingListItem>())
                    call.respond(HttpStatusCode.OK)
                }
                delete("/{id}") {
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
//                    collection.deleteOne(ShoppingListItem::id eq id)
                    collection.removeIf(Predicate { t ->  t.id == id})
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }.start(wait = true)
}