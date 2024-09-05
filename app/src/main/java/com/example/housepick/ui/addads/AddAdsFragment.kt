package com.example.housepick.ui.addads

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.housepick.R
import com.example.housepick.classes.Housing
import com.example.housepick.databinding.FragmentAddAdsBinding
import com.example.housepick.ui.utils.showSnackBar
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AddAdsFragment : Fragment() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextStreet: EditText
    private lateinit var editTextPostalCode: EditText
    private lateinit var editTextCity: EditText
    private lateinit var editTextCountry: EditText
    private lateinit var editTextEstateType: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextEstatePrice: EditText
    private lateinit var editTextNumberBathroom: EditText
    private lateinit var editTextNumberBed: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var imgToUpload: ImageView
    private lateinit var createAdsButton: Button

    private lateinit var addAdsViewModel: AddAdsViewModel
    private var _binding: FragmentAddAdsBinding? = null

    private var uri: Uri? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        addAdsViewModel = ViewModelProvider(this).get(AddAdsViewModel::class.java)

        _binding = FragmentAddAdsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        createAdsButton = binding.createAdsButton
        createAdsButton.isEnabled = false

        val switchRent: Switch = binding.switchRent

        editTextTitle = binding.editTextTitle
        editTextStreet = binding.editTextStreet
        editTextPostalCode = binding.editTextPostalCode
        editTextCountry = binding.editTextCountry
        editTextCity = binding.editTextCity
        editTextDescription = binding.editTextDescription
        editTextEstateType = binding.editTextEstateType
        editTextEstatePrice = binding.editTextEstatePrice
        editTextNumberBathroom = binding.editTextNumberBathroom
        editTextNumberBed = binding.editTextNumberBed
        editTextEmail = binding.editTextEmail
        editTextPhone = binding.editTextPhone
        imgToUpload = binding.imgToUpload

        editTextTitle.addTextChangedListener(textWatcher)
        editTextStreet.addTextChangedListener(textWatcher)
        editTextCity.addTextChangedListener(textWatcher)
        editTextPostalCode.addTextChangedListener(textWatcher)
        editTextCountry.addTextChangedListener(textWatcher)
        editTextDescription.addTextChangedListener(textWatcher)
        editTextEstateType.addTextChangedListener(textWatcher)
        editTextEstatePrice.addTextChangedListener(textWatcher)
        editTextNumberBathroom.addTextChangedListener(textWatcher)
        editTextNumberBed.addTextChangedListener(textWatcher)
        editTextEmail.addTextChangedListener(textWatcher)
        editTextPhone.addTextChangedListener(textWatcher)

        addAdsViewModel.getAction().observe(
            viewLifecycleOwner
        ) { action -> action?.let { handleAction(it) } }


        createAdsButton.setOnClickListener {
            val title = editTextTitle.text.toString()
            val street = editTextStreet.text.toString()
            val city = editTextCity.text.toString()
            val postalCode = editTextPostalCode.text.toString()
            val country = editTextCountry.text.toString()
            val desc = editTextDescription.text.toString()
            val estateType = editTextEstateType.text.toString()
            val estatePrice = editTextEstatePrice.text.toString()
            val numberBath = editTextNumberBathroom.text.toString()
            val numberBed = editTextNumberBed.text.toString()
            val email = editTextEmail.text.toString()
            val phone = editTextPhone.text.toString()
            val rent = switchRent.isChecked
            val housing = Housing(
                title,
                street,
                city,
                postalCode,
                country,
                estatePrice,
                estateType,
                rent,
                numberBath,
                numberBed,
                email,
                phone,
                desc,
                null,
                null,
                null,
                null
            )

            val latLng = LatLng(32.63647365027362, 51.67652963465183)
            housing.latLong = latLng

            if (uri != null) {
                val imageStream: InputStream? = context?.contentResolver?.openInputStream(uri!!)
                if (imageStream != null) {
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    val encodedImage: String? = encodeImage(selectedImage)
                    if (encodedImage != null) {
                        binding.createAdsButton.isEnabled = false
                        //housing.imgPath = uri.toString()
                        housing.imgPath = encodedImage
                        addAdsViewModel.createAd(housing)
                    }
                }
            } else {
                showSnackBar(binding.root, R.string.no_image_selected, R.drawable.mail_box_icon)
                //Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imgAddButton.setOnClickListener {
            openActivityForResult()
        }

        binding.imageButtonBed.setOnClickListener {
            binding.editTextNumberBed.requestFocus()
        }
        binding.imageButtonBathroom.setOnClickListener {
            binding.editTextNumberBathroom.requestFocus()
        }
        binding.imageButtonGarage.setOnClickListener {
            binding.editTextNumberGarage.requestFocus()
        }

        return root
    }

    private fun handleAction(action: Action) {
        when (action.value) {
            Action.SHOW_AD_CREATED -> {
                binding.createAdsButton.isEnabled = true
                editTextTitle.setText("")
                editTextStreet.setText("")
                editTextCity.setText("")
                editTextPostalCode.setText("")
                editTextCountry.setText("")
                editTextDescription.setText("")
                editTextEstateType.setText("")
                editTextEstatePrice.setText("")
                editTextNumberBathroom.setText("")
                editTextNumberBed.setText("")
                editTextEmail.setText("")
                editTextPhone.setText("")
                showSnackBar(binding.root, R.string.ad_created, R.drawable.mail_box_icon)
                //Toast.makeText(context, "Ad created", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.navigation_ads)
            }

            Action.SHOW_INVALID_FORM -> {
                binding.createAdsButton.isEnabled = true
                showSnackBar(binding.root, R.string.invalid_form, R.drawable.mail_box_icon)
                //Toast.makeText(context, "Invalid form", Toast.LENGTH_SHORT).show()
            }

            Action.SHOW_BAD_ADDRESS -> {
                binding.createAdsButton.isEnabled = true
                showSnackBar(binding.root, R.string.address_doesnt_exist, R.drawable.mail_box_icon)
                //Toast.makeText(context, "This address doesn't exist", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun encodeImage(bm: Bitmap): String? {
        val os = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, os)
        val b: ByteArray = os.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            checkFieldsForEmptyValues()
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    private fun checkFieldsForEmptyValues() {
        val s1: String = editTextTitle.text.toString()
        val s2: String = editTextStreet.text.toString()
        val s10: String = editTextCity.text.toString()
        val s11: String = editTextPostalCode.text.toString()
        val s12: String = editTextCountry.text.toString()
        val s3: String = editTextDescription.text.toString()
        val s4: String = editTextEstateType.text.toString()
        val s5: String = editTextEstatePrice.text.toString()
        val s6: String = editTextNumberBathroom.text.toString()
        val s7: String = editTextNumberBed.text.toString()
        val s8: String = editTextEmail.text.toString()
        val s9: String = editTextPhone.text.toString()
        createAdsButton.isEnabled = s1.isNotEmpty() && s2.isNotEmpty() &&
                s3.isNotEmpty() && s4.isNotEmpty() && s5.isNotEmpty()
                && s6.isNotEmpty() && s7.isNotEmpty() && s8.isNotEmpty()
                && s9.isNotEmpty() && s10.isNotEmpty() && s11.isNotEmpty()
                && s12.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val res: Intent? = result.data
                if (res != null) {
                    val extra = res.data
                    uri = extra
                    val takeFlags: Int =
                        res.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    requireContext().contentResolver.takePersistableUriPermission(uri!!, takeFlags)
                    imgToUpload.setImageURI(extra)
                }
            }
        }

    private fun openActivityForResult() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startForResult.launch(intent)
    }

}