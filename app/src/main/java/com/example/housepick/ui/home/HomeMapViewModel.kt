package com.example.housepick.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.housepick.data.httpServices.Ads
import com.example.housepick.data.httpServices.VolleyCallbackAds
import org.json.JSONArray
import org.json.JSONObject

class HomeMapViewModel: ViewModel() {
    private val mAction: MutableLiveData<Action> = MutableLiveData<Action>()
    var homesArray = JSONArray()

    fun getAction(): LiveData<Action> {
        return mAction
    }
    private val ads = Ads()

    fun displayHomesByCity(city: String){
        val cb: VolleyCallbackAds = object: VolleyCallbackAds {
            override fun onSuccessObject(result: JSONObject) {
                println(result)
                homesArray = result.getJSONArray("housings") as JSONArray
                showDataLoaded()
            }

            override fun onSuccessArray(result: JSONArray) {
                homesArray = result
                showDataLoaded()
            }

            override fun onError() {
                showNetworkError()
            }

        }
        ads.getHousesByCity(city, cb)
    }

    fun displayHomesByArea(latitude: Double, longitude: Double){
        val cb: VolleyCallbackAds = object: VolleyCallbackAds {
            override fun onSuccessObject(result: JSONObject) {
                homesArray = result.get("housings") as JSONArray
                showDataLoaded()
            }

            override fun onSuccessArray(result: JSONArray) {
                homesArray = result
                showDataLoaded()
            }

            override fun onError() {
                showNetworkError()
            }

        }
        ads.getHousesAroundMe(latitude,longitude,cb)
    }
    private fun showDataLoaded() {
        mAction.value = Action(Action.HOMES_LOADED)

    }
    private fun showNetworkError() {
        mAction.value = Action(Action.NETWORK_ERROR)
    }



}
