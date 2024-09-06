package com.example.housepick.ui.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.housepick.MainActivity
import com.example.housepick.MyApplication
import com.example.housepick.R
import com.example.housepick.databinding.FragmentNotificationsBinding
import com.example.housepick.ui.utils.LocaleUtils
import com.example.housepick.ui.utils.showSnackBar
import java.util.Locale

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    private lateinit var languageSpinner: Spinner

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        notificationsViewModel.getAction().observe(
            viewLifecycleOwner
        ) { action -> action?.let { handleAction(it) } }

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        languageSpinner = binding.languageSpinner
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_options,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter
        val sharedPreferences =
            requireContext().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val defaultLanguage = LocaleUtils.getSelectedLanguageId()

        // Set spinner based on current language
        languageSpinner.setSelection(if (defaultLanguage == LocaleUtils.PERSIAN) 1 else 0)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedLanguage =
                    if (position == 0) LocaleUtils.ENGLISH else LocaleUtils.PERSIAN
                if (selectedLanguage != LocaleUtils.getSelectedLanguageId()) {
                    LocaleUtils.setSelectedLanguageId(requireContext(), selectedLanguage)
                    restartActivity()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val fullName = sharedPreferences.getString("fullName", "N/A")
        val email = sharedPreferences.getString("email", "N/A")
        binding.profileFullName.text = fullName
        binding.profileEmail.text = email

        val disconnectButton: Button = binding.btnDisconnect
        binding.profileSwitchAllowNotifications.isChecked = MyApplication.allowNotifications

        binding.profileSwitchAllowNotifications.setOnCheckedChangeListener { _, isChecked ->
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("allowNotifs", isChecked)
            myEdit.apply()
            if (isChecked) {
                MyApplication.allowNotifications = true
                binding.profileImageView.setBackgroundResource(R.drawable.profile_picture_agency)
            } else {
                MyApplication.allowNotifications = false
                binding.profileImageView.setBackgroundResource(R.drawable.profile_picture)
            }
        }

        binding.profileButtonPasswordUpdate.setOnClickListener {
            binding.profileButtonPasswordUpdate.isEnabled = false
            val oldPassword: String = binding.profileEditTextOldPassword.text.toString()
            val newPassword: String = binding.profileEditTextNewPassword.text.toString()
            notificationsViewModel.changePassword(oldPassword, newPassword)
        }

        disconnectButton.setOnClickListener {
            MyApplication.JWT = null
            val myEdit = sharedPreferences.edit()
            myEdit.putString("jwt", null)
            myEdit.apply()
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            startActivity(mainActivityIntent)
            activity?.finish()
        }

        return root
    }

    private fun restartActivity() {
        val currentActivity = requireActivity()

        // Create a new configuration with the updated locale
        val config = currentActivity.resources.configuration
        val locale = LocaleUtils.getSelectedLanguageId()
        val newLocale = Locale(locale)
        Locale.setDefault(newLocale)
        config.setLocale(newLocale)

        // Set the layout direction for RTL languages
        if (locale == LocaleUtils.PERSIAN) {
            config.setLayoutDirection(newLocale)
        } else {
            config.setLayoutDirection(Locale.ENGLISH)
        }

        // Apply the new configuration
        currentActivity.resources.updateConfiguration(
            config,
            currentActivity.resources.displayMetrics
        )

        // Restart the activity
        val intent = currentActivity.intent
        currentActivity.finish()
        currentActivity.startActivity(intent)
    }

    private fun handleAction(action: Action) {
        when (action.value) {
            Action.SHOW_SUCCESS -> {
                binding.profileButtonPasswordUpdate.isEnabled = true
                showSnackBar(binding.root, R.string.password_changed, R.drawable.mail_box_icon)
            }

            Action.SHOW_ERROR -> {
                binding.profileButtonPasswordUpdate.isEnabled = true
                showSnackBar(binding.root, R.string.error_from_server, R.drawable.mail_box_icon)
            }

            Action.SHOW_ERROR_MUST_CORRESPOND -> {
                binding.profileButtonPasswordUpdate.isEnabled = true
                showSnackBar(
                    binding.root,
                    R.string.passwords_must_correspond,
                    R.drawable.mail_box_icon
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
