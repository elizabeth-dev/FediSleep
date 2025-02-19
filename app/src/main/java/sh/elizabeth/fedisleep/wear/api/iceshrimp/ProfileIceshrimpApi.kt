package sh.elizabeth.fedisleep.wear.api.iceshrimp

import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.fedisleep.api.iceshrimp.model.Profile_UpdateSelf
import javax.inject.Inject

class ProfileIceshrimpApi @Inject constructor(private val httpClient: HttpClient) {
    suspend fun updateSelf(
        endpoint: String,
        token: String,
        name: String,
    ) {
        httpClient.post("https://$endpoint/api/i/update") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(Profile_UpdateSelf(name))
        }
    }
}
