package com.example.housepick.ui.ads

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.housepick.Application
import com.example.housepick.R
import com.example.housepick.databinding.FragmentOneAdBinding
import org.json.JSONObject


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
        oneAdViewModel =
            ViewModelProvider(this).get(OneAdViewModel::class.java)

        _binding = FragmentOneAdBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val id: Int = arguments?.getInt("id")!!

        oneAdViewModel.getAction().observe(
            viewLifecycleOwner
        ) { action -> action?.let { handleAction(it) } }

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

    private fun handleAction(action: OneAdAction) {
        when (action.value) {
            OneAdAction.HOME_LOADED -> {
                ad = oneAdViewModel.ad
                binding.adDetailsTitle.setText(ad.getString("title"))
                binding.adDetailsAddress.setText(
                    ad.getString("street") + ", " + ad.getString("city") + ", " + ad.getString(
                        "country"
                    )
                )
                binding.adDetailsPrice.setText("$" + ad.getInt("estateprice"))
                binding.adDetailsEstateType.setText(
                    ad.getString("estatetype") + " for " + if (ad.getBoolean(
                            "rent"
                        )
                    ) "rent" else "sell"
                )
                binding.adDetailsBedNumber.setText(ad.getInt("numberbed").toString())
                binding.adDetailsBathNumber.setText(ad.getInt("numberbath").toString())
//                binding.adDetailsCarNumber.setText(home.getInt("numbercar").toString())
                binding.adDetailsEmail.setText(ad.getString("email"))
                binding.adDetailsPhone.setText(ad.getString("phone"))
                binding.adDetailsDescription.setText(ad.getString("description"))

                val img = binding.adDetailsImage
                //Toast.makeText(context, "Please wait for the image, it may take a few seconds...",     Toast.LENGTH_SHORT).show()
                DownloadImageFromInternet(img).execute(ad.getString("imgpath"))
            }

            OneAdAction.NETWORK_ERROR -> {
                if (Application.isActivityVisible()) {
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                }
            }

            OneAdAction.DELETE_SUCCESS -> {
                findNavController().navigate(R.id.navigation_ads)
                Toast.makeText(context, "Housing successfully deleted", Toast.LENGTH_SHORT).show()
            }

            OneAdAction.DELETE_ERROR -> {
                Toast.makeText(context, "Couldn't delete the housing.", Toast.LENGTH_SHORT).show()
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

