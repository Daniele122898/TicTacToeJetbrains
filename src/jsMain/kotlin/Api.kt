import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.window

val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved

val jsonClient = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

suspend fun getCreateGame(): Game {
    return jsonClient.get(endpoint + GameRoute)
}

// TODO technically it would be WAAY better to just sent updates on actual moves
// especially with some form of websocket. But this is waaaay to out of scope
// and i simply dont have the time :/
suspend fun getGame(id: String): Game {
    return jsonClient.get(endpoint + GameRoute + "/${id}")
}

suspend fun postGameMove(id: String, move: Move) {
    jsonClient.post<Unit>(endpoint + GameRoute + "/${id}") {
        contentType(ContentType.Application.Json)
        body = move
    }
}