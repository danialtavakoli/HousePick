package com.example.housepick.classes

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
    var numberParking: String,
    var email: String,
    var phone: String,
    var description: String,
    var latitude: String?,
    var longitude: String?,
    var imgPath: String?,
    var priceTag: String?,
    var closestHomeId: String?
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
        jsonObject.put("numberParking", numberParking.toInt())
        jsonObject.put("email", email)
        jsonObject.put("phone", phone)
        jsonObject.put("rent", rent)
        jsonObject.put("image", imgPath)
        if (latitude != null && longitude != null) {
            jsonObject.put("latitude", latitude!!.toDouble())
            jsonObject.put("longitude", longitude!!.toDouble())
        }
        return jsonObject
    }
}