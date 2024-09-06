package com.example.housepick.ui.ads

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.housepick.MyApplication
import com.example.housepick.R
import com.example.housepick.databinding.FragmentOneAdBinding
import com.example.housepick.ui.utils.LocaleUtils
import com.example.housepick.ui.utils.showImage
import com.example.housepick.ui.utils.showSnackBar
import org.json.JSONObject
import java.util.Locale


class OneAdFragment : Fragment() {
    private lateinit var oneAdViewModel: OneAdViewModel
    private var _binding: FragmentOneAdBinding? = null

    private var ad = JSONObject()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        oneAdViewModel = ViewModelProvider(this).get(OneAdViewModel::class.java)

        _binding = FragmentOneAdBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val id: Int = arguments?.getInt("id")!!

        oneAdViewModel.getAction().observe(viewLifecycleOwner) { action ->
            action?.let {
                handleAction(
                    it, root.context
                )
            }
        }

        oneAdViewModel.displayHome(id)

        binding.deleteButton.setOnClickListener {
            oneAdViewModel.deleteHousing(id)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleAction(action: OneAdAction, context: Context) {
        when (action.value) {
            OneAdAction.HOME_LOADED -> {
                ad = oneAdViewModel.ad
                updateAdDetails(ad, context)

                val img = binding.adDetailsImage
                showImage(ad.getString("image"), img)
            }

            OneAdAction.NETWORK_ERROR -> {
                if (MyApplication.isActivityVisible()) {
                    showSnackBar(binding.root, R.string.network_error, R.drawable.phone_icon)
                }
            }

            OneAdAction.DELETE_SUCCESS -> {
                findNavController().navigate(R.id.navigation_ads)
                showSnackBar(
                    binding.root,
                    R.string.housing_deleted_success,
                    R.drawable.ant_design_home_outlined
                )
            }

            OneAdAction.DELETE_ERROR -> {
                showSnackBar(
                    binding.root,
                    R.string.housing_delete_failed,
                    R.drawable.ant_design_home_outlined
                )
            }
        }
    }

    // Helper function to update ad details
    private fun updateAdDetails(ad: JSONObject, context: Context) {
        val textRent = context.getString(R.string.rent)
        val textSell = context.getString(R.string.sell)
        val rentStatus = if (ad.getBoolean("rent")) textRent else textSell
        val textFor = context.getString(R.string.text_for)
        val textRial = context.getString(R.string.rial)

        // Get price status string resources
        val priceStatusLabel = context.getString(R.string.price_status)
        val normalStatus = context.getString(R.string.normal_status)

        val price = ad.getInt("statePrice")
        val bedNumber = ad.getInt("numberBed")
        val bathNumber = ad.getInt("numberBath")
        val numberParking = ad.getInt("numberParking")

        binding.adDetailsTitle.text = ad.getString("title")
        binding.adDetailsAddress.text =
            formatAddress(ad.getString("street"), ad.getString("city"), ad.getString("country"))

        // Set price text without price status
        binding.adDetailsPrice.text = formatPrice(price, textRial)

        // Set price status in the new TextView
        binding.adDetailsPriceStatus.text = "$priceStatusLabel $normalStatus"

        binding.adDetailsEstateType.text =
            formatEstateType(ad.getString("stateType"), textFor, rentStatus)
        binding.adDetailsBedNumber.text = formatNumber(bedNumber)
        binding.adDetailsBathNumber.text = formatNumber(bathNumber)
        binding.adDetailsCarNumber.text = formatNumber(numberParking)
        binding.adDetailsEmail.text = ad.getString("email")
        binding.adDetailsPhone.text = ad.getString("phone")
        binding.adDetailsDescription.text = ad.getString("description")
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
