import com.versa.english.data.api.ApiConfig
import com.versa.english.data.model.request_models.ChatRequest
import com.versa.english.data.model.response_models.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGPTService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${ApiConfig.API_KEY}"
    )
    @POST("chat/completions")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}

