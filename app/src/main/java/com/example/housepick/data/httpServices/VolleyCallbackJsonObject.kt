package com.example.housepick.data.httpServices

import org.json.JSONObject

interface VolleyCallbackJsonObject {
    fun onSuccess(result: JSONObject?)
    fun onError()
}