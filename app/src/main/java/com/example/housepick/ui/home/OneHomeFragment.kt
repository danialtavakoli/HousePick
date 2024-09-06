package com.example.housepick.ui.home

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
import com.example.housepick.databinding.FragmentOneHomeBinding
import com.example.housepick.ui.utils.LocaleUtils
import com.example.housepick.ui.utils.showImage
import com.example.housepick.ui.utils.showSnackBar
import org.json.JSONObject
import java.util.Locale

class OneHomeFragment : Fragment() {
    private lateinit var oneHomeViewModel: OneHomeViewModel
    private var _binding: FragmentOneHomeBinding? = null
    private val binding get() = _binding!!

    private var home = JSONObject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        oneHomeViewModel = ViewModelProvider(this).get(OneHomeViewModel::class.java)
        _binding = FragmentOneHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val id: Int = arguments?.getInt("id") ?: 0

        // Observing actions from ViewModel
        oneHomeViewModel.getAction().observe(viewLifecycleOwner) { action ->
            action?.let {
                handleAction(it, root.context)
            }
        }

        // Fetch and display the home details for the provided ID
        oneHomeViewModel.displayHome(id)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Handling actions when data is loaded or errors occur
    private fun handleAction(action: OneHomeAction, context: Context) {
        when (action.value) {
            OneHomeAction.HOME_LOADED -> {
                // Home loaded, update the UI with the home details
                home = oneHomeViewModel.home
                updateHomeDetails(home, context)

                val img = binding.adDetailsImage
                showImage(home.getString("image"), img)

                // Handle closestHomeId (recommended ad)
                val closestHomeId = home.optInt("closestHomeId", -1)
                if (closestHomeId != -1) {
                    // If a valid closestHomeId is present, display the recommended ad
                    oneHomeViewModel.displayHome(closestHomeId, isRecommendedAd = true)
                } else {
                    binding.recommendedAdCard.visibility = View.GONE
                }
            }

            OneHomeAction.RECOMMENDED_HOME_LOADED -> {
                // Recommended home loaded, update the recommended ad section
                val recommendedHome = oneHomeViewModel.recommendedHome
                updateRecommendedAdDetails(recommendedHome)

                val img = binding.recommendedAdImage
                showImage(recommendedHome.getString("image"), img)

                // Set click listener to navigate to the recommended ad details
                binding.recommendedAdCard.setOnClickListener {
                    navigateToHome(recommendedHome.getInt("id"))
                }
            }

            OneHomeAction.NETWORK_ERROR -> {
                if (MyApplication.isActivityVisible()) {
                    showSnackBar(binding.root, R.string.network_error, R.drawable.mail_box_icon)
                }
            }
        }
    }

    // Method to handle navigation when a user clicks on the recommended ad
    private fun navigateToHome(id: Int) {
        val bundle = Bundle().apply {
            putInt("id", id)  // Pass the recommended ad id to the fragment
        }
        findNavController().navigate(R.id.action_oneHomeFragment_self, bundle)
    }

    // Update recommended ad details in the UI
    private fun updateRecommendedAdDetails(home: JSONObject) {
        binding.recommendedAdTitle.text = home.getString("title")
        binding.recommendedAdDetails.text = home.getString("description")
    }

    // Helper function to update home details
    private fun updateHomeDetails(home: JSONObject, context: Context) {
        val textRent = context.getString(R.string.rent)
        val textSell = context.getString(R.string.sell)
        val rentStatus = if (home.getBoolean("rent")) textRent else textSell
        val textFor = context.getString(R.string.text_for)
        val textRial = context.getString(R.string.rial)

        // Get price status string resources
        val priceStatusLabel = context.getString(R.string.price_status)
        val normalStatus = context.getString(R.string.normal_status)

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

        // Set price text without price status
        binding.adDetailsPrice.text = formatPrice(price, textRial)

        // Set price status in the new TextView
        binding.adDetailsPriceStatus.text = "$priceStatusLabel $normalStatus"

        binding.adDetailsEstateType.text =
            formatEstateType(home.getString("stateType"), textFor, rentStatus)
        binding.adDetailsBedNumber.text = formatNumber(bedNumber)
        binding.adDetailsBathNumber.text = formatNumber(bathNumber)
        binding.adDetailsCarNumber.text = formatNumber(numberParking)
        binding.adDetailsEmail.text = home.getString("email")
        binding.adDetailsPhone.text = home.getString("phone")
        binding.adDetailsDescription.text = home.getString("description")
    }

    // Helper functions to format price, numbers, estate type, and address
    private fun formatPrice(price: Int, textRial: String): String {
        val locale = LocaleUtils.getSelectedLanguageId()
        return String.format(Locale(locale), "%d %s", price, textRial)
    }

    private fun formatNumber(number: Int): String {
        val locale = LocaleUtils.getSelectedLanguageId()
        return String.format(Locale(locale), "%d", number)
    }

    private fun formatEstateType(estateType: String, textFor: String, rentStatus: String): String {
        return "$estateType $textFor $rentStatus"
    }

    private fun formatAddress(street: String, city: String, country: String): String {
        return "$street, $city, $country"
    }
}
