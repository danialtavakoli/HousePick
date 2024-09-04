package com.example.housepick.data.httpServices

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.housepick.Application
import com.example.housepick.classes.Housing
import org.json.JSONObject

class Ads {

    private var BOUNDARY =
        "s2retfgsGSRFsERFGHfgdfgw734yhFHW567TYHSrf4yarg" // This is the boundary which is used by the server to split the post parameters.
    private var MULTIPART_FORMDATA = "multipart/form-data;boundary=$BOUNDARY"

    fun getHouse(callback: VolleyCallbackAds, id: Int) {
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://${Application.IP}/ad/$id"

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            { response -> callback.onSuccessObject(response) },
            { _ -> callback.onError() }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer " + Application.JWT
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }
        queue.add(jsonRequest)
    }

    fun getHouses(callback: VolleyCallbackAds) {
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://${Application.IP}/ad/all"
        println(Application.JWT)

        val jsonRequest: JsonArrayRequest = object : JsonArrayRequest(
            Method.GET, url, null,
            { response -> callback.onSuccessArray(response)
            }, { callback.onError() })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer " + Application.JWT
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }
        println(jsonRequest.url.toString())
        println(jsonRequest.headers.toString())
        queue.add(jsonRequest)
    }

    fun getMyHouses(callback: VolleyCallbackAds) {
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://${Application.IP}/ad/all/user"

        val jsonArrayRequest: JsonArrayRequest = object : JsonArrayRequest(
            Method.GET, url, null,
            { response ->
                // Handle the JSONArray response
                callback.onSuccessArray(response)
            },
            { error ->
                callback.onError()
                println(error.message)
                println(error.cause?.message.toString())
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer " + Application.JWT
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }
        queue.add(jsonArrayRequest)
    }

    fun getHousesByCity(city: String, callback: VolleyCallbackAds) {
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://${Application.IP}/ad/all?city=$city"

        val jsonRequest: JsonArrayRequest = object : JsonArrayRequest(
            Method.GET, url, null,
            { response -> callback.onSuccessArray(response) },
            { error ->
                callback.onError()
                println(error.message)
                println(error.cause?.message.toString())
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer " + Application.JWT
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }
        queue.add(jsonRequest)
    }

    fun getHousesAroundMe(latitude: Double, longitude: Double, callback: VolleyCallbackAds) {
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://${Application.IP}/housings/area/$longitude&$latitude&20"

        val jsonRequest: JsonArrayRequest = object : JsonArrayRequest(
            Method.GET, url, null,
            { response -> callback.onSuccessArray(response) },
            { _ -> callback.onError() }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer " + Application.JWT
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }
        queue.add(jsonRequest)
    }

    fun hostImage(b64Image: String, cb: VolleyCallbackJsonObject) {
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://${Application.IP}/upload"

        val jsonObject = JSONObject()

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonObject,
            Response.Listener { response ->
                cb.onSuccess(response)
            },
            Response.ErrorListener {
                cb.onError()
            }) {

            override fun getBodyContentType(): String? {
                return MULTIPART_FORMDATA
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray? {
                val map: MutableMap<String, String> = HashMap()
                map["image"] = b64Image
                return createPostBody(map)!!.toByteArray()
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = MULTIPART_FORMDATA
                params["Authorization"] = "Bearer " + Application.JWT
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }

        queue.add(jsonRequest)
    }

    fun createAd(
        housing: Housing,
        callback: VolleyCallbackJsonObject
    ) {
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://${Application.IP}/ad"

        val jsonObject = housing.toJsonObject()

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonObject,
            Response.Listener { response -> callback.onSuccess(response) },
            Response.ErrorListener { callback.onError() }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer " + Application.JWT
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }
        queue.add(jsonRequest)
    }

    fun deleteHousing(id: Int, cb: VolleyCallbackAds) {
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://${Application.IP}/ad/$id"

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.DELETE, url, null,
            Response.Listener { response -> cb.onSuccessObject(response) },
            Response.ErrorListener { cb.onError() }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer " + Application.JWT
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }

            override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                return if (response != null && response.data.isNotEmpty()) {
                    super.parseNetworkResponse(response)
                } else {
                    // Treat an empty or blank response as an empty JSON object
                    Response.success(JSONObject(), HttpHeaderParser.parseCacheHeaders(response))
                }
            }
        }
        queue.add(jsonRequest)
    }

    private fun createPostBody(params: Map<String, String?>?): String? {
        val sbPost = StringBuilder()
        if (params != null) {
            for (key in params.keys) {
                if (params[key] != null) {
                    sbPost.append("\r\n--$BOUNDARY\r\n")
                    sbPost.append("Content-Disposition: form-data; name=\"$key\"\r\n\r\n")
                    sbPost.append(params[key].toString())
                }
            }
        }
        return sbPost.toString()
    }
}
