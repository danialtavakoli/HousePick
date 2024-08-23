package com.example.housepick.classes

import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

class Housing(
    var title: String,
    var street: String,
    var city: String,
    var postalCode: String,
    var country: String,
    var estatePrice: String,
    var estateType: String,
    var rent: Boolean,
    var numberBath: String,
    var numberBed: String,
    var email: String,
    var phone: String,
    var description: String,
    var latLong: LatLng?,
    var imgPath: String?
) {


    fun toJsonObject(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("title", title)
        jsonObject.put("street", street)
        jsonObject.put("city", city)
        jsonObject.put("postalCode", postalCode)
        jsonObject.put("country", country)
        jsonObject.put("description", description)
        jsonObject.put("stateType", estateType)
        jsonObject.put("statePrice", estatePrice.toFloat())
        jsonObject.put("numberBath", numberBath.toInt())
        jsonObject.put("numberBed", numberBed.toInt())
        jsonObject.put("email", email)
        jsonObject.put("phone", phone)
        jsonObject.put("rent", rent)
        jsonObject.put(
            "latLong", JSONObject().put("longitude", latLong!!.longitude)
                .put("latitude", latLong!!.latitude).toString()
        )
        if (imgPath !== null) {
            jsonObject.put("imgPath", imgPath)
        }
        return jsonObject
    }
}