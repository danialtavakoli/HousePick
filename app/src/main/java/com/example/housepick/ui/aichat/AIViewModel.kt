package com.example.housepick.ui.aichat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.housepick.R
import com.example.housepick.classes.Message
import com.example.housepick.data.httpServices.AIService
import com.example.housepick.data.httpServices.VolleyCallbackJsonObject
import org.json.JSONObject

class AIViewModel(application: Application) : AndroidViewModel(application) {

    private val aiService = AIService()

    // List of messages to display in the chat
    val messages: MutableList<Message> = mutableListOf()

    private val _messageLiveData = MutableLiveData<List<Message>>()
    val messageLiveData: LiveData<List<Message>> get() = _messageLiveData

    // Function to send a message and get a response from the AI
    fun sendMessageToAI(message: String) {
        // Add user's message to the list
        messages.add(Message(message, true))
        _messageLiveData.value = messages

        // Send the message to AI
        aiService.sendMessage(message, object : VolleyCallbackJsonObject {
            override fun onSuccess(result: JSONObject?) {
                val aiResponse = result?.optString("result", "No response") ?: "No response"
                // Add AI's message to the list
                messages.add(Message(aiResponse, false))
                _messageLiveData.value = messages
            }

            override fun onError() {
                val errorMessage = getApplication<Application>().getString(R.string.network_error)
                messages.add(Message(errorMessage, false))
                _messageLiveData.value = messages
            }
        })
    }
}