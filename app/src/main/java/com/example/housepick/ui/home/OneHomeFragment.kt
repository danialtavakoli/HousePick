package com.example.housepick.ui.home

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
import com.bumptech.glide.Glide
import com.example.housepick.Application
import com.example.housepick.R
import com.example.housepick.databinding.FragmentOneHomeBinding
import com.example.housepick.ui.utils.showSnackBar
import org.json.JSONObject
import java.text.NumberFormat
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

    @SuppressLint("SetTextI18n")
    private fun handleAction(action: OneHomeAction, context: Context) {
        when (action.value) {
            OneHomeAction.HOME_LOADED -> {
                home = oneHomeViewModel.home
                println(home.toString())
                val textRent = context.getString(R.string.rent)
                val textSell = context.getString(R.string.sell)
                val rent = if (home.getBoolean("rent")) textRent else textSell
                val textFor = context.getString(R.string.text_for)
                val textRial = context.getString(R.string.rial)
                val price = home.getInt("statePrice")
                val street = home.getString("street")
                val city = home.getString("city")
                val country = home.getString("country")
                val estateType = home.getString("stateType")
                val bedNumber = home.getInt("numberBed")
                val bathNumber = home.getInt("numberBath")
                val email = home.getString("email")
                val phone = home.getString("phone")
                val description = home.getString("description")
                val title = home.getString("title")
                val imagePath = home.getString("image")

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
                //Toast.makeText(context, "Please wait for the image, it may take a few seconds...",     Toast.LENGTH_SHORT).show()
                //DownloadImageFromInternet(img).execute(home.getString("imgpath"))

                // Load the image using Glide
                Glide.with(context)
                    .load(imagePath)
                    .into(img)
            }

            OneHomeAction.NETWORK_ERROR -> {
                if (Application.isActivityVisible()) {
                    showSnackBar(binding.root, R.string.network_error, R.drawable.mail_box_icon)
                    //Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    class DownloadImageFromInternet(private var imageView: ImageView) :
        AsyncTask<String, Void, Bitmap?>() {
        //        init {
//            Toast.makeText(context, "Please wait for the image, it may take a few seconds...",     Toast.LENGTH_SHORT).show()
//        }
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

