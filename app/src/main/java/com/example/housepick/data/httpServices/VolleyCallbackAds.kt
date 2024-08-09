package com.example.housepick.data.httpServices

import org.json.JSONArray
import org.json.JSONObject

interface VolleyCallbackAds {
    fun onSuccessObject(result : JSONObject)
    fun onSuccessArray(result : JSONArray)
    fun onError()
}