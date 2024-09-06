package com.example.housepick.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.housepick.data.httpServices.Ads
import com.example.housepick.data.httpServices.VolleyCallbackAds
import org.json.JSONArray
import org.json.JSONObject

class OneHomeViewModel : ViewModel() {
    private val mAction: MutableLiveData<OneHomeAction> = MutableLiveData<OneHomeAction>()

    var home = JSONObject()
    var recommendedHome = JSONObject()

    fun getAction(): LiveData<OneHomeAction> {
        return mAction
    }

    private val ads = Ads()

    fun displayHome(id: Int, isRecommendedAd: Boolean = false) {
        val cb: VolleyCallbackAds = object : VolleyCallbackAds {
            override fun onSuccessObject(result: JSONObject) {
                if (result != null) {
                    if (isRecommendedAd) {
                        recommendedHome = result
                        mAction.value = OneHomeAction(OneHomeAction.RECOMMENDED_HOME_LOADED)
                    } else {
                        home = result
                        mAction.value = OneHomeAction(OneHomeAction.HOME_LOADED)
                    }
                }
            }

            override fun onSuccessArray(result: JSONArray) {
                // Not used
            }

            override fun onError() {
                mAction.value = OneHomeAction(OneHomeAction.NETWORK_ERROR)
            }
        }
        ads.getHouse(cb, id)
    }
}

class OneHomeAction(val value: Int) {
    companion object {
        const val HOME_LOADED = 0
        const val NETWORK_ERROR = 1
        const val RECOMMENDED_HOME_LOADED = 2
    }
}