package com.example.housepick.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.housepick.MyApplication
import com.example.housepick.R
import com.example.housepick.databinding.FragmentOneHomeBinding
import com.example.housepick.ui.utils.LocaleUtils
import com.example.housepick.ui.utils.showImage
import com.example.housepick.ui.utils.showSnackBar
import org.json.JSONObject
import java.util.Locale


class OneHomeFragment : Fragment() {
    private lateinit var oneHomeViewModel: OneHomeViewModel
    private var _binding: FragmentOneHomeBinding? = null

    private var home = JSONObject()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        oneHomeViewModel = ViewModelProvider(this).get(OneHomeViewModel::class.java)

        _binding = FragmentOneHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val id: Int = arguments?.getInt("id")!!
        oneHomeViewModel.sendClickedAdId(id)

        oneHomeViewModel.getAction().observe(viewLifecycleOwner) { action ->
            action?.let {
                handleAction(
                    it,
                    root.context
                )
            }
        }

        oneHomeViewModel.displayHome(id)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleAction(action: OneHomeAction, context: Context) {
        when (action.value) {
            OneHomeAction.HOME_LOADED -> {
                home = oneHomeViewModel.home
                updateHomeDetails(home, context)

                val img = binding.adDetailsImage
                showImage(home.getString("image"), img)
            }

            OneHomeAction.NETWORK_ERROR -> {
                if (MyApplication.isActivityVisible()) {
                    showSnackBar(binding.root, R.string.network_error, R.drawable.mail_box_icon)
                }
            }
        }
    }

    // Helper function to update home details
    private fun updateHomeDetails(home: JSONObject, context: Context) {
        val textRent = context.getString(R.string.rent)
        val textSell = context.getString(R.string.sell)
        val rentStatus = if (home.getBoolean("rent")) textRent else textSell
        val textFor = context.getString(R.string.text_for)
        val textRial = context.getString(R.string.rial)

        val price = home.getInt("statePrice")
        val bedNumber = home.getInt("numberBed")
        val bathNumber = home.getInt("numberBath")
        val numberParking = home.getInt("numberParking")

        binding.adDetailsTitle.text = home.getString("title")
        binding.adDetailsAddress.text = formatAddress(
            home.getString("street"),
            home.getString("city"),
            home.getString("country")
        )
        binding.adDetailsPrice.text = formatPrice(price, textRial)
        binding.adDetailsEstateType.text =
            formatEstateType(home.getString("stateType"), textFor, rentStatus)
        binding.adDetailsBedNumber.text = formatNumber(bedNumber)
        binding.adDetailsBathNumber.text = formatNumber(bathNumber)
        binding.adDetailsCarNumber.text = formatNumber(numberParking)
        binding.adDetailsEmail.text = home.getString("email")
        binding.adDetailsPhone.text = home.getString("phone")
        binding.adDetailsDescription.text = home.getString("description")
    }

    // Helper function to format price
    private fun formatPrice(price: Int, textRial: String): String {
        val locale = LocaleUtils.getSelectedLanguageId()
        return String.format(Locale(locale), "%d %s", price, textRial)
    }

    // Helper function to format numbers (bedNumber, bathNumber)
    private fun formatNumber(number: Int): String {
        val locale = LocaleUtils.getSelectedLanguageId()
        return String.format(Locale(locale), "%d", number)
    }

    // Helper function to format estate type with rent/sell status
    private fun formatEstateType(estateType: String, textFor: String, rentStatus: String): String {
        return "$estateType $textFor $rentStatus"
    }

    // Helper function to format address
    private fun formatAddress(street: String, city: String, country: String): String {
        return "$street, $city, $country"
    }

}
