package com.example.housepick.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.housepick.MyApplication
import com.example.housepick.R
import com.example.housepick.databinding.FragmentMainHomeBinding


class MainHomeFragment : Fragment() {
    private var _binding: FragmentMainHomeBinding? = null

    val homeFragment = HomeFragment()
    val homeMapFragment = HomeMapFragment()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val transaction = parentFragmentManager.beginTransaction()
        switchToList()

        binding.listOrMapSwitch.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                switchToMap()
            }else{
                switchToList()
            }
        }
        return root
    }

    private fun switchToList() {

        val transaction = parentFragmentManager.beginTransaction()
        MyApplication.homeListOrMap = false
        transaction.replace(R.id.mainHomeLayout, homeFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun switchToMap() {

        val transaction = parentFragmentManager.beginTransaction()
        MyApplication.homeListOrMap = true
        transaction.replace(R.id.mainHomeLayout, homeMapFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}