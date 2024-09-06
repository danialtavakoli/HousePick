package com.example.housepick.data.httpServices

import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.housepick.MyApplication

class AIService {

    fun sendMessage(message: String, callback: VolleyCallbackJsonObject) {
        val queue = Volley.newRequestQueue(MyApplication.appContext)
        val url =
            "https://api3.haji-api.ir/lic/gpt/4?q=$message&license=k5varPleXpAWNB3RXEoNfxg3ajz4SAaOADw2ZW6jBaeu1WDXYW"

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            { response -> callback.onSuccess(response) },
            { callback.onError() }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json; charset=UTF-8"
                return params
            }
        }

        queue.add(jsonRequest)
    }
}
