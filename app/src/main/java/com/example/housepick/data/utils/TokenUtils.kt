package com.example.housepick.data.utils

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import com.example.housepick.MyApplication

class TokenUtils {
    companion object {
        fun checkIfExpired(): Boolean {
            val sharedPreferences: SharedPreferences? =
                MyApplication.appContext?.getSharedPreferences(
                    "MySharedPref",
                    AppCompatActivity.MODE_PRIVATE
                )
            val token: String? = sharedPreferences?.getString("jwt", null)
            if (token != null) {
                MyApplication.JWT = token
                val jwt = JWT(token)
                return (jwt.isExpired(10))
            }
            return true
        }

        fun getId(): Int? {
            val sharedPreferences: SharedPreferences? =
                MyApplication.appContext?.getSharedPreferences(
                    "MySharedPref",
                    AppCompatActivity.MODE_PRIVATE
                )
            val token: String? = sharedPreferences?.getString("jwt", null)
            return if (token != null) {
                val jwt = JWT(token)
                (jwt.getClaim("sub").asInt()!!)
            } else {
                null
            }
        }

    }
}