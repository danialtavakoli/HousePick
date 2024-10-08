package com.example.housepick.ui.login

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.housepick.AppActivity
import com.example.housepick.MyApplication
import com.example.housepick.R
import com.example.housepick.databinding.FragmentLoginBinding
import com.example.housepick.ui.utils.showSnackBar


class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var loginViewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null

    private lateinit var loginButton: Button

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        loginButton = binding.btnLogin

        loginViewModel.getAction().observe(
            viewLifecycleOwner
        ) { action -> action?.let { handleAction(it) } }

        loginButton.setOnClickListener {
            loginButton.isEnabled = false
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            loginViewModel.userWantToLogin(password, email)
        }

        return root
    }

    private fun handleAction(action: Action) {
        when (action.value) {
            Action.SHOW_WELCOME -> {
                val jwt = MyApplication.JWT
                val sharedPreferences: SharedPreferences =
                    requireActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE)
                val myEdit = sharedPreferences.edit()
                myEdit.putString("jwt", jwt.toString())
                myEdit.apply()
                val appActivityIntent = Intent(activity, AppActivity::class.java)
                startActivity(appActivityIntent)
                activity?.finish()
            }

            Action.SHOW_INVALID_PASSWARD_OR_LOGIN -> {
                loginButton.isEnabled = true
                showSnackBar(binding.root, R.string.bad_email_password, R.drawable.mail_box_icon)
                //Toast.makeText(context, R.string.bad_email_password, Toast.LENGTH_SHORT).show()
            }
        }
    }


}