package com.example.housepick.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.housepick.data.httpServices.Authentication
import com.example.housepick.data.httpServices.VolleyCallbackJsonObject
import org.json.JSONObject

class NotificationsViewModel : ViewModel() {
    private val authentication = Authentication()


    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    private val mAction: MutableLiveData<Action> = MutableLiveData<Action>()

    fun getAction(): LiveData<Action> {
        return mAction
    }
    val text: LiveData<String> = _text

    fun changePassword(password: String, repassword: String) {
        if(password == repassword){
            val cb: VolleyCallbackJsonObject = object: VolleyCallbackJsonObject {
                override fun onSuccess(result: JSONObject?) {
                    showSuccess()
                }
                override fun onError() {
                    showErrorChangingPassword()
                }
            }
            authentication.changePassword(password, cb)
        }else{
            showPasswordMustCorrespond()
        }


    }

    private fun showErrorChangingPassword() {
        mAction.value = Action(Action.SHOW_ERROR)
    }


    private fun showSuccess() {
        mAction.value = Action(Action.SHOW_SUCCESS)
    }
    private fun showPasswordMustCorrespond() {
        mAction.value = Action(Action.SHOW_ERROR_MUST_CORRESPOND)
    }
}


class Action(val value: Int) {

    companion object {
        const val SHOW_SUCCESS = 0
        const val SHOW_ERROR = 1
        const val SHOW_ERROR_MUST_CORRESPOND = 2

    }
}