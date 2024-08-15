package com.example.housepick.data.httpServices

import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.housepick.Application
import org.json.JSONObject

class Authentication {

    private val queue = Volley.newRequestQueue(Application.appContext)
    private val urlLogin = "http://" + Application.IP + "/auth/login"
    private val urlSignup = "http://" + Application.IP + "/auth/signup"

    fun login(mail: String, password: String, callbackJsonObject: VolleyCallbackJsonObject) {
        val jsonObject = JSONObject()
        jsonObject.put("username", mail)
        jsonObject.put("password", password)
        val jsonRequest = JsonObjectRequest(Request.Method.POST, urlLogin, jsonObject, { response ->
            callbackJsonObject.onSuccess(response)
        }, { err ->
            println(err)
            callbackJsonObject.onError()
        })
        queue.add(jsonRequest)
    }


    fun register(
        name: String,
        mail: String,
        password: String,
        repassword: String,
        callbackJsonObject: VolleyCallbackJsonObject
    ) {
        val jsonObject = JSONObject()
        jsonObject.put("username", mail)
        jsonObject.put("fullname", name)
        jsonObject.put("password", password)
        jsonObject.put("role", "CUSTOMER")
        val jsonRequest =
            JsonObjectRequest(Request.Method.POST, urlSignup, jsonObject, { response ->
                callbackJsonObject.onSuccess(response)
            }, { err ->
                println(err)
                callbackJsonObject.onError()
            })
        queue.add(jsonRequest)
    }

    fun changePassword(
        password: String, callback: VolleyCallbackJsonObject
    ) {
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://" + Application.IP + "/users/" + Application.getID()

        val jsonObject = JSONObject()
        jsonObject.put("password", password)

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(Method.PATCH,
            url,
            jsonObject,
            Response.Listener { response -> callback.onSuccess(response) },
            Response.ErrorListener { callback.onError() }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer " + Application.JWT
                return params
            }
        }
        queue.add(jsonRequest)
    }

}
