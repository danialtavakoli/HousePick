package com.example.housepick.ui.addads

import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.housepick.MyApplication
import com.example.housepick.classes.Housing
import com.example.housepick.data.httpServices.Ads
import com.example.housepick.data.httpServices.VolleyCallbackJsonObject
import org.json.JSONObject
import java.util.Locale

class AddAdsViewModel : ViewModel() {
    private val ads = Ads()
    private val geocoder: Geocoder = Geocoder(MyApplication.appContext!!, Locale.getDefault())

    private val mAction: MutableLiveData<Action> = MutableLiveData<Action>()

    fun getAction(): LiveData<Action> {
        return mAction
    }

    fun createAd(housing: Housing) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(housing.email).matches()) {
            showBadMail()
        } else {
            val addHousingCb: VolleyCallbackJsonObject = object : VolleyCallbackJsonObject {
                override fun onSuccess(result: JSONObject?) {
                    showAdsCreated()
                }

                override fun onError() {
                    showInvalidArguments()
                }
            }
            ads.createAd(housing, addHousingCb)
        }
    }

    /*fun createAd(housing: Housing, b64Image: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(housing.email).matches()) {
            showBadMail()
        } else {
            val uploadImageCb: VolleyCallbackJsonObject = object : VolleyCallbackJsonObject {
                override fun onSuccess(result: JSONObject?) {
                    println(result.toString())
                    val addHousingCb: VolleyCallbackJsonObject =
                        object : VolleyCallbackJsonObject {
                            override fun onSuccess(result: JSONObject?) {
                                showAdsCreated()
                            }

                            override fun onError() {
                                showInvalidArguments()
                            }
                        }
                    println("onSuccess")
                    var valid = false
                    if (result != null) {
                        val data = result.get("data") as JSONObject?
                        if (data != null) {
                            val imgUrl = data.get("url") as String?
                            if (imgUrl != null) {
                                valid = true
                                println(imgUrl)
                                housing.imgPath = imgUrl
                                ads.createAd(housing, addHousingCb)
                            }
                        }
                    }
                    if (!valid) {
                        showInvalidArguments()
                    }
                }

                override fun onError() {
                    showInvalidArguments()
                }
            }
            ads.hostImage(b64Image, uploadImageCb)
        }
    }*/

    private fun showInvalidArguments() {
        mAction.value = Action(Action.SHOW_INVALID_FORM)
    }

    private fun showAdsCreated() {
        mAction.value = Action(Action.SHOW_AD_CREATED)
    }

    private fun showBadAddress() {
        mAction.value = Action(Action.SHOW_BAD_ADDRESS)
    }

    private fun showBadMail() {
        mAction.value = Action(Action.SHOW_BAD_MAIL)
    }
}


class Action(val value: Int) {

    companion object {
        const val SHOW_AD_CREATED = 0
        const val SHOW_INVALID_FORM = 1
        const val SHOW_BAD_ADDRESS = 2
        const val SHOW_BAD_MAIL = 3
    }
}