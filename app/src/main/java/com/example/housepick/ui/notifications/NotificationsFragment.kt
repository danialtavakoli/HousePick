package com.example.housepick.ui.notifications

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.housepick.Application
import com.example.housepick.MainActivity
import com.example.housepick.R
import com.example.housepick.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
        notificationsViewModel.getAction().observe(
            viewLifecycleOwner
        ) { action -> action?.let { handleAction(it) } }

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val disconnectButton: Button = binding.btnDisconnect
        binding.profileSwitchAllowNotifications.isChecked = Application.allowNotifications

        binding.profileSwitchAllowNotifications.setOnCheckedChangeListener { _, isChecked ->
            val sharedPreferences: SharedPreferences? =
                Application.appContext?.getSharedPreferences(
                    "MySharedPref",
                    AppCompatActivity.MODE_PRIVATE
                )
            val myEdit = sharedPreferences!!.edit()
            myEdit.putBoolean("allowNotifs", isChecked)
            myEdit.apply()
            if (isChecked) {
                Application.allowNotifications = true
                binding.profileImageView.setBackgroundResource(R.drawable.profile_picture_agency)
            } else {
                Application.allowNotifications = false
                binding.profileImageView.setBackgroundResource(R.drawable.profile_picture)
            }
        }
        binding.profileButtonPasswordUpdate.setOnClickListener {
            binding.profileButtonPasswordUpdate.isEnabled = false
            val password: String = binding.profileEditTextPassword.text.toString()
            val repassword: String = binding.profileEditTextPasswordRetype.text.toString()
            notificationsViewModel.changePassword(password, repassword)
        }

        disconnectButton.setOnClickListener {
            Application.JWT = null
            val sharedPreferences: SharedPreferences =
                requireActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putString("jwt", null)
            myEdit.apply()
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            startActivity(mainActivityIntent)
            activity?.finish()
        }

//        val textView: TextView = binding.textNotifications
//        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    private fun handleAction(action: Action) {
        when (action.value) {
            Action.SHOW_SUCCESS -> {
                binding.profileButtonPasswordUpdate.isEnabled = true
                Toast.makeText(context, "Password changed", Toast.LENGTH_SHORT).show()
            }

            Action.SHOW_ERROR -> {
                binding.profileButtonPasswordUpdate.isEnabled = true
                Toast.makeText(context, "Error from server", Toast.LENGTH_SHORT).show()
            }

            Action.SHOW_ERROR_MUST_CORRESPOND -> {
                binding.profileButtonPasswordUpdate.isEnabled = true
                Toast.makeText(context, "Passwords must correspond", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}