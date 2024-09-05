package com.example.housepick.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.housepick.data.httpServices.Ads
import com.example.housepick.data.httpServices.VolleyCallbackAds
import com.example.housepick.data.httpServices.VolleyCallbackJsonObject
import org.json.JSONArray
import org.json.JSONObject

class OneHomeViewModel : ViewModel() {
    private val mAction: MutableLiveData<OneHomeAction> = MutableLiveData<OneHomeAction>()

    var home = JSONObject()

    fun getAction(): LiveData<OneHomeAction> {
        return mAction
    }
    private val ads = Ads()

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    fun displayHome(id : Int){
        val cb: VolleyCallbackAds = object: VolleyCallbackAds {
            override fun onSuccessObject(result: JSONObject) {
                if (result != null) {
                    home = result
                }
                showDataLoaded()
            }
            override fun onSuccessArray(result: JSONArray) {
                // Not used
            }
            override fun onError() {
                showNetworkError()
            }
        }
        ads.getHouse(cb, id)
    }


    fun sendClickedAdId(adId: Int) {
        val cb = object : VolleyCallbackJsonObject {
            override fun onSuccess(result: JSONObject?) {
                // Handle success (optional)
                println("Ad click registered successfully.")
            }

            override fun onError() {
                // Handle error
                println("Failed to register ad click.")
            }
        }
        println("id: $adId")
        //ads.sendClickedAd(adId, cb)
    }


    private fun showDataLoaded() {
        mAction.value = OneHomeAction(OneHomeAction.HOME_LOADED)
    }

    private fun showNetworkError() {
        mAction.value = OneHomeAction(OneHomeAction.NETWORK_ERROR)
    }
}

class OneHomeAction(val value: Int) {
    companion object {
        const val HOME_LOADED = 0
        const val NETWORK_ERROR = 1
    }
}