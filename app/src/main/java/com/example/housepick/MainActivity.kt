package com.example.housepick

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.housepick.data.utils.TokenUtils
import com.example.housepick.databinding.ActivityMainBinding
import com.example.housepick.ui.login.LoginFragment
import com.example.housepick.ui.register.RegisterFragment

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MyApplication.getInstance().initAppLanguage(this)

        val authenticationStateAdapter = AuthenticationStateAdapter(this)
        authenticationStateAdapter.addFragment(LoginFragment())
        authenticationStateAdapter.addFragment(RegisterFragment())
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = authenticationStateAdapter
        val isExpired: Boolean = TokenUtils.checkIfExpired()
        if (!isExpired) {
            val appActivityIntent = Intent(applicationContext, AppActivity::class.java)
            startActivity(appActivityIntent)
            this.finish()
        }
    }

    // Function to switch to the LoginFragment
    fun switchToLoginFragment() {
        viewPager.currentItem = 0  // Assuming LoginFragment is at position 0
    }

    class AuthenticationStateAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
        private val fragmentList: ArrayList<Fragment> = ArrayList()

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getItemCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment) {
            fragmentList.add(fragment)
        }
    }

    override fun onResume() {
        super.onResume()
        MyApplication.activityResumed()
    }

    override fun onPause() {
        super.onPause()
        MyApplication.activityPaused()
    }
}