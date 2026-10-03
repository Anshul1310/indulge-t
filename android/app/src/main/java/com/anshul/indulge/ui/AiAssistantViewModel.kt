package com.anshul.indulge.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshul.indulge.models.Event
import com.anshul.indulge.models.RecommendRequest
import com.anshul.indulge.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val events: List<Event> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

class AiAssistantViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                isUser = false,
                text = "Hey there! 👋 Tell me your mood, city, or budget (e.g. 'chill acoustic vibes under 600' or 'lonely in Bangalore') and I'll find the best events for you."
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isBlank() || _isTyping.value) return

        val userMessage = ChatMessage(isUser = true, text = trimmed)
        _messages.value = _messages.value + userMessage
        _isTyping.value = true

        viewModelScope.launch {
            try {
                val request = RecommendRequest(prompt = trimmed)
                val response = ApiClient.apiService.getRecommendations(request)

                val replyText = response.message ?: if (response.data.isEmpty()) {
                    "I couldn't find any events matching your criteria. Try adjusting your mood, city, or budget!"
                } else {
                    "Here are events matching your vibe:"
                }

                _messages.value = _messages.value + ChatMessage(
                    isUser = false,
                    text = replyText,
                    events = response.data
                )
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    isUser = false,
                    text = "Oops! Something went wrong connecting to the server (${e.localizedMessage ?: "Network error"}). Please try again."
                )
            } finally {
                _isTyping.value = false
            }
        }
    }
}
