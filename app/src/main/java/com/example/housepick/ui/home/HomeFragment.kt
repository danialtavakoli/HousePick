package com.example.housepick.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.housepick.Application
import com.example.housepick.R
import com.example.housepick.databinding.FragmentHomeBinding
import com.example.housepick.ui.utils.showSnackBar
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private var housesArray = JSONArray()
    private val viewAdapter = MyAdapter(housesArray)


    private lateinit var swipeContainer: SwipeRefreshLayout


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.listHouses.run {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
        binding.homeSearchBtn.setOnClickListener {
            if (binding.homeSearch.text.toString().isNotEmpty()) {
                homeViewModel.displayHomesByCity(binding.homeSearch.text.toString())
            } else {
                homeViewModel.displayHomes()
            }
        }

        homeViewModel.getAction().observe(
            viewLifecycleOwner
        ) { action -> action?.let { handleAction(it) } }

        homeViewModel.displayHomes()

        swipeContainer = binding.swipeContainerAllHouses

        swipeContainer.setOnRefreshListener {
            homeViewModel.displayHomes()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleAction(action: Action) {
        when (action.value) {
            Action.HOMES_LOADED -> {
                //Toast.makeText(context, "Please wait for the images, it may take a few seconds...", Toast.LENGTH_LONG).show()
                viewAdapter.swapDataSet(homeViewModel.homesArray)
                swipeContainer.isRefreshing = false
            }

            Action.NETWORK_ERROR -> {
                if (Application.isActivityVisible()) {
                    showSnackBar(
                        binding.root, R.string.no_houses_found, R.drawable.ant_design_home_outlined
                    )
                    //Toast.makeText(context, R.string.no_houses_found, Toast.LENGTH_SHORT).show()
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
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_house, parent, false)

        return ViewHolder(itemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val house = myDataset[position] as JSONObject

        // Extract data from JSONObject
        val id = house.getInt("id")
        val price = house.getInt("statePrice")
        val street = house.getString("street")
        val city = house.getString("city")
        val country = house.getString("country")
        val estateType = house.getString("stateType")
        val bedNumber = house.getInt("numberBed")
        val bathNumber = house.getInt("numberBath")
//        val carNumber = house.getInt("numberCar")
        val textRent = holder.itemView.context.getString(R.string.rent)
        val textSell = holder.itemView.context.getString(R.string.sell)
        val rent = if (house.getBoolean("rent")) textRent else textSell
        val textFor = holder.itemView.context.getString(R.string.text_for)
        val textRial = holder.itemView.context.getString(R.string.rial)

        val numberFormat = NumberFormat.getNumberInstance(Locale("fa", "IR"))
        val persianPrice = numberFormat.format(price)
        val persianBedNumber = numberFormat.format(bedNumber).toString()
        val persianBathNumber = numberFormat.format(bathNumber).toString()

        holder.item.findViewById<TextView>(R.id.adItemPrice).text = "$persianPrice $textRial"
        holder.item.findViewById<TextView>(R.id.adItemAddress).text = "$street, $city, $country"
        holder.item.findViewById<TextView>(R.id.adItemBedNumber).text = persianBedNumber
        holder.item.findViewById<TextView>(R.id.adItemBathNumber).text = persianBathNumber
//        holder.item.findViewById<TextView>(R.id.adItemCarNumber).text = carNumber.toString()
        holder.item.findViewById<TextView>(R.id.adItemEstateType).text =
            "$estateType $textFor $rent"

        //val img = holder.item.findViewById<ImageView>(R.id.adItemImage)
        //OneHomeFragment.DownloadImageFromInternet(img).execute(house.getString("imgpath"))

        holder.item.setOnClickListener {
            val bundle = bundleOf("id" to id)
            holder.item.findNavController().navigate(
                R.id.action_navigation_home_to_oneHomeFragment, bundle
            )
        }
    }

    fun swapDataSet(newData: JSONArray) {
        myDataset = newData

        notifyDataSetChanged()
    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.length()

}