package com.example.housepick.data.httpServices

import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.housepick.Application
import com.example.housepick.classes.Housing
import org.json.JSONObject

import java.lang.StringBuilder
import kotlin.collections.HashMap


class Ads {

    private var BOUNDARY =
        "s2retfgsGSRFsERFGHfgdfgw734yhFHW567TYHSrf4yarg" //This the boundary which is used by the server to split the post parameters.
    private var MULTIPART_FORMDATA = "multipart/form-data;boundary=$BOUNDARY"

    fun getHouse(callback: VolleyCallbackAds, id: Int){
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://" + Application.IP + "/housings/" + id.toString()

        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                callback.onSuccessObject(response)
            },
            { _ ->
                callback.onError()
            })
        queue.add(jsonRequest)
    }

    fun getHouses( callback: VolleyCallbackAds) {
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://" + Application.IP + "/housings"

        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                callback.onSuccessArray(response)
            },
            { _ ->
                callback.onError()
            })
        queue.add(jsonRequest)
    }

    fun getMyHouses(callback: VolleyCallbackAds ){
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://" + Application.IP + "/housings/user"

        val jsonRequest : JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            { response ->
                callback.onSuccessObject(response)
            },
            { err ->
                callback.onError()
            }) {
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

    fun getHousesByCity(city: String, callback: VolleyCallbackAds) {
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://" + Application.IP + "/housings/city/"+city

        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                callback.onSuccessObject(response)
            },
            { _ ->
                callback.onError()
            })
        queue.add(jsonRequest)

    }

    fun getHousesArroundMe(latitude: Double, longitude: Double, callback: VolleyCallbackAds) {
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://" + Application.IP + "/housings/area/"+longitude+"&"+latitude+"&"+20

        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                callback.onSuccessObject(response)
            },
            { _ ->
                callback.onError()
            })
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

    fun hostImage(b64Image : String, cb : VolleyCallbackJsonObject){
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "https://api.imgbb.com/1/upload?key=${"ce5f8c85e3cbe0c433c7002c95659dcb"}"

        val jsonObject = JSONObject()

        val jsonRequest : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonObject,
            Response.Listener { response -> cb.onSuccess(response) },
            Response.ErrorListener { cb.onError() }) {
            override fun getBodyContentType(): String? {
                return MULTIPART_FORMDATA
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray? {
                val map : MutableMap<String,String> = HashMap()
                map["image"] = b64Image
                return createPostBody(map)!!.toByteArray()
            }
        }

        queue.add(jsonRequest)
    }

    fun createAd(housing: Housing,
                   callback: VolleyCallbackJsonObject ){
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://" + Application.IP + "/housings"

        val jsonObject = housing.toJsonObject()
        println(jsonObject)

        val jsonRequest : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonObject,
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

    fun deleteHousing(id : Int, cb : VolleyCallbackAds){
        val queue = Volley.newRequestQueue(Application.appContext)
        val url = "http://" + Application.IP + "/housings/" + id.toString()
        val jsonObject = JSONObject()
        val jsonRequest : JsonObjectRequest = object : JsonObjectRequest(
            Method.DELETE, url, jsonObject,
            Response.Listener { response -> cb.onSuccessObject(response) },
            Response.ErrorListener { cb.onError() }) {
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