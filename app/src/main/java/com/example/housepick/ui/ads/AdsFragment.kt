package com.example.housepick.ui.ads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.housepick.MyApplication
import com.example.housepick.R
import com.example.housepick.databinding.FragmentAdsBinding
import com.example.housepick.ui.addads.AddAdsFragment
import com.example.housepick.ui.utils.LocaleUtils
import com.example.housepick.ui.utils.showImage
import com.example.housepick.ui.utils.showSnackBar
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale


class AdsFragment : Fragment() {

    private lateinit var adsViewModel: AdsViewModel
    private var _binding: FragmentAdsBinding? = null

    private var adsArray = JSONArray()
    private val viewAdapter = MyAdapter(adsArray)


    private lateinit var swipeContainer: SwipeRefreshLayout

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        adsViewModel = ViewModelProvider(this).get(AdsViewModel::class.java)

        _binding = FragmentAdsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.listAds.run {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        adsViewModel.getAction().observe(
            viewLifecycleOwner
        ) { action -> action?.let { handleAction(it) } }

        adsViewModel.displayAds()

        swipeContainer = binding.swipeContainerAllAds

        swipeContainer.setOnRefreshListener {
            adsViewModel.displayAds()
        }

        binding.floatingAddButton.setOnClickListener {
            parentFragmentManager.commit {
                replace(container!!.id, AddAdsFragment())
                setReorderingAllowed(true)
                addToBackStack("com.example.housepick.ui.addads.AddAdsFragment in the place")
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleAction(action: AdAction) {
        when (action.value) {
            AdAction.HOMES_LOADED -> {
                //Toast.makeText(context, "Please wait for the images, it may take a few seconds...", Toast.LENGTH_LONG).show()
                viewAdapter.swapDataSet(adsViewModel.adsArray)
                swipeContainer.isRefreshing = false
            }

            AdAction.NETWORK_ERROR -> {
                if (MyApplication.isActivityVisible()) {
                    showSnackBar(binding.root, R.string.network_error, R.drawable.mail_box_icon)
                    //Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show()
                }
                swipeContainer.isRefreshing = false
            }
        }
    }
}

class MyAdapter(private var myDataset: JSONArray) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val item: View) : RecyclerView.ViewHolder(item)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        // create a new view
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_ad, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ad = myDataset[position] as JSONObject

        // Extract data from JSONObject
        val id = ad.getInt("id")
        val price = ad.getInt("statePrice")
        val street = ad.getString("street")
        val city = ad.getString("city")
        val country = ad.getString("country")
        val estateType = ad.getString("stateType")
        val bedNumber = ad.getInt("numberBed")
        val bathNumber = ad.getInt("numberBath")
        val numberParking = ad.getInt("numberParking")
        val imagePath = ad.getString("image")
        val rentStatus = getRentStatus(holder, ad.getBoolean("rent"))

        // Set text to views
        holder.apply {
            setText(R.id.adItemPrice, formatPrice(holder, price))
            setText(R.id.adItemAddress, "$street, $city, $country")
            setText(R.id.adItemBedNumber, formatNumber(bedNumber))
            setText(R.id.adItemBathNumber, formatNumber(bathNumber))
            setText(R.id.adItemCarNumber, formatNumber(numberParking))
            setText(
                R.id.adItemEstateType,
                "$estateType ${holder.itemView.context.getString(R.string.text_for)} $rentStatus"
            )

            // Load image
            val img = item.findViewById<ImageView>(R.id.adItemImage)
            showImage(imagePath, img)

            // Handle item click event
            item.setOnClickListener {
                navigateToDetail(holder, id)
            }
        }
    }

    // Helper function to format price based on locale
    private fun formatPrice(holder: ViewHolder, price: Int): String {
        val local = LocaleUtils.getSelectedLanguageId()
        val textRial = holder.itemView.context.getString(R.string.rial)
        return String.format(Locale(local), "%d %s", price, textRial)
    }

    // Helper function to format bedNumber and bathNumber based on locale
    private fun formatNumber(number: Int): String {
        val local = LocaleUtils.getSelectedLanguageId()
        return String.format(Locale(local), "%d", number)
    }

    // Helper function to get rent status
    private fun getRentStatus(holder: ViewHolder, isRent: Boolean): String {
        val textRent = holder.itemView.context.getString(R.string.rent)
        val textSell = holder.itemView.context.getString(R.string.sell)
        return if (isRent) textRent else textSell
    }

    // Helper function to set text on a TextView
    private fun ViewHolder.setText(viewId: Int, text: String) {
        item.findViewById<TextView>(viewId).text = text
    }

    // Helper function to navigate to detail view
    private fun navigateToDetail(holder: ViewHolder, id: Int) {
        val bundle = bundleOf("id" to id)
        holder.item.findNavController()
            .navigate(R.id.action_navigation_ads_to_oneAdFragment, bundle)
    }

    fun swapDataSet(newData: JSONArray) {
        myDataset = newData

        notifyDataSetChanged()
    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.length()

}