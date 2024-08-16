package com.example.housepick.ui.home

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.housepick.Application
import com.example.housepick.R
import com.example.housepick.databinding.FragmentOneHomeBinding
import org.json.JSONObject


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
    ): View? {
        oneHomeViewModel =
            ViewModelProvider(this).get(OneHomeViewModel::class.java)

        _binding = FragmentOneHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val id: Int = arguments?.getInt("id")!!

        oneHomeViewModel.getAction()?.observe(viewLifecycleOwner,
            Observer<OneHomeAction> { action -> action?.let { handleAction(it) } })

        oneHomeViewModel.displayHome(id)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleAction(action: OneHomeAction) {
        when (action.value) {
            OneHomeAction.HOME_LOADED -> {
                home = oneHomeViewModel.home
                binding.adDetailsTitle.setText(home.getString("title"))
                binding.adDetailsAddress.setText(
                    home.getString("street") + ", " + home.getString("city") + ", " + home.getString(
                        "country"
                    )
                )
                binding.adDetailsPrice.setText("$" + home.getInt("estateprice"))
                binding.adDetailsEstateType.setText(
                    home.getString("estatetype") + " for " + if (home.getBoolean(
                            "rent"
                        )
                    ) "rent" else "sell"
                )
                binding.adDetailsBedNumber.setText(home.getInt("numberbed").toString())
                binding.adDetailsBathNumber.setText(home.getInt("numberbath").toString())
//                binding.adDetailsCarNumber.setText(home.getInt("numbercar").toString())
                binding.adDetailsEmail.setText(home.getString("email"))
                binding.adDetailsPhone.setText(home.getString("phone"))
                binding.adDetailsDescription.setText(home.getString("description"))

                val img = binding.adDetailsImage
                //Toast.makeText(context, "Please wait for the image, it may take a few seconds...",     Toast.LENGTH_SHORT).show()
                DownloadImageFromInternet(img).execute(home.getString("imgpath"))
            }

            OneHomeAction.NETWORK_ERROR -> {
                if (Application.isActivityVisible()) {
                    Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
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

