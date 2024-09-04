package com.example.housepick.ui.ads

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.housepick.Application
import com.example.housepick.R
import com.example.housepick.databinding.FragmentOneAdBinding
import com.example.housepick.ui.utils.showImage
import com.example.housepick.ui.utils.showSnackBar
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale


class OneAdFragment : Fragment() {
    private lateinit var oneAdViewModel: OneAdViewModel
    private var _binding: FragmentOneAdBinding? = null

    private var ad = JSONObject()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        oneAdViewModel = ViewModelProvider(this).get(OneAdViewModel::class.java)

        _binding = FragmentOneAdBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val id: Int = arguments?.getInt("id")!!

        oneAdViewModel.getAction().observe(viewLifecycleOwner) { action ->
            action?.let {
                handleAction(
                    it,
                    root.context
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

    @SuppressLint("SetTextI18n")
    private fun handleAction(action: OneAdAction, context: Context) {
        when (action.value) {
            OneAdAction.HOME_LOADED -> {
                ad = oneAdViewModel.ad
                val textRent = context.getString(R.string.rent)
                val textSell = context.getString(R.string.sell)
                val rent = if (ad.getBoolean("rent")) textRent else textSell
                val textFor = context.getString(R.string.text_for)
                val textRial = context.getString(R.string.rial)
                val price = ad.getInt("statePrice")
                val street = ad.getString("street")
                val city = ad.getString("city")
                val country = ad.getString("country")
                val estateType = ad.getString("stateType")
                val bedNumber = ad.getInt("numberBed")
                val bathNumber = ad.getInt("numberBath")
                val email = ad.getString("email")
                val phone = ad.getString("phone")
                val description = ad.getString("description")
                val title = ad.getString("title")
                val imagePath = ad.getString("image")

                val numberFormat = NumberFormat.getNumberInstance(Locale("fa", "IR"))
                val persianPrice = numberFormat.format(price)
                val persianBedNumber = numberFormat.format(bedNumber).toString()
                val persianBathNumber = numberFormat.format(bathNumber).toString()

                binding.adDetailsTitle.text = title
                binding.adDetailsAddress.text = "$street, $city, $country"
                binding.adDetailsPrice.text = "$persianPrice $textRial"
                binding.adDetailsEstateType.text = "$estateType $textFor $rent"
                binding.adDetailsBedNumber.text = persianBedNumber
                binding.adDetailsBathNumber.text = persianBathNumber
//                binding.adDetailsCarNumber.setText(home.getInt("numbercar").toString())
                binding.adDetailsEmail.text = email
                binding.adDetailsPhone.text = phone
                binding.adDetailsDescription.text = description

                val img = binding.adDetailsImage
                showImage(imagePath, img)
            }

            OneAdAction.NETWORK_ERROR -> {
                if (Application.isActivityVisible()) {
                    showSnackBar(binding.root, R.string.network_error, R.drawable.phone_icon)
                    //Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show()
                }
            }

            OneAdAction.DELETE_SUCCESS -> {
                findNavController().navigate(R.id.navigation_ads)
                showSnackBar(
                    binding.root,
                    R.string.housing_deleted_success,
                    R.drawable.ant_design_home_outlined
                )
                //Toast.makeText(context, R.string.housing_deleted_success, Toast.LENGTH_SHORT).show()
            }

            OneAdAction.DELETE_ERROR -> {
                showSnackBar(
                    binding.root,
                    R.string.housing_delete_failed,
                    R.drawable.ant_design_home_outlined
                )
                //Toast.makeText(context, R.string.housing_delete_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    class DownloadImageFromInternet(private var imageView: ImageView) :
        AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }

        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }
}

