package com.example.housepick.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.housepick.MyApplication
import com.example.housepick.data.httpServices.Authentication
import com.example.housepick.data.httpServices.VolleyCallbackJsonObject
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    private val authentication = Authentication()

    //Stores actions for view.
    private val mAction: MutableLiveData<Action> = MutableLiveData<Action>()

    fun getAction(): LiveData<Action> {
        return mAction
    }

    fun userWantToLogin(password: String, login: String) {
        val cb: VolleyCallbackJsonObject = object : VolleyCallbackJsonObject {
            override fun onSuccess(result: JSONObject?) {
                if (result != null) {
                    val jsonResult = result.get("result") as JSONObject
                    MyApplication.JWT = jsonResult.get("token").toString()

                    val fullName = jsonResult.getString("fullname")
                    val email = jsonResult.getString("username")
                    // Store full name and email in SharedPreferences
                    val sharedPreferences = MyApplication.appContext?.getSharedPreferences(
                        "MySharedPref",
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreferences?.edit()
                    editor?.putString("fullName", fullName)
                    editor?.putString("email", email)
                    editor?.apply()
                }
                showWelcomeScreen()
            }

            override fun onError() {
                showPasswordOrLoginInvalid()
            }
        }
        authentication.login(login, password, cb)
    }


    /*
         * Changes LiveData. Does not act directly with view.
         * View can implement any way to show info
          * to user (show new activity, alert or toast)
         */
    private fun showPasswordOrLoginInvalid() {
        mAction.value = Action(Action.SHOW_INVALID_PASSWARD_OR_LOGIN)
    }

    /*
         * Changes LiveData. Does not act directly with view.
         * View can implement any way to show info
         * to user (show new activity, alert or toast)
         */
    private fun showWelcomeScreen() {
        mAction.value = Action(Action.SHOW_WELCOME)
    }


}

class Action(val value: Int) {

    companion object {
        const val SHOW_WELCOME = 0
        const val SHOW_INVALID_PASSWARD_OR_LOGIN = 1
    }
}