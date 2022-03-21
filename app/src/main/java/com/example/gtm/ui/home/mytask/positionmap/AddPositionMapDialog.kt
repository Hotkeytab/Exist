package com.example.gtm.ui.home.mytask.positionmap


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.gtm.R
import com.example.gtm.data.entities.response.DataXX
import com.example.gtm.data.entities.response.GetStore
import com.example.gtm.data.entities.response.ModifyStoreResponse
import com.example.gtm.ui.home.mytask.LocationValueListener
import com.example.gtm.ui.home.mytask.StaticMapClicked
import com.example.gtm.ui.home.mytask.addvisite.AddVisiteDialogViewModel
import com.example.gtm.utils.remote.Internet.InternetCheck
import com.example.gtm.utils.remote.Internet.InternetCheckDialog
import com.example.gtm.utils.resources.Resource
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.gms.maps.CameraUpdate
import kotlinx.android.synthetic.main.fragment_position_map.*
import kotlinx.android.synthetic.main.item_task.*
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody


@AndroidEntryPoint
class AddPositionMapDialog(
    name: String,
    store2: DataXX
) :
    DialogFragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var nameIn = name
    private lateinit var newPosition: LatLng
    private val store = store2
    private val viewModel: AddPositionMapDialogViewModel by viewModels()
    private lateinit var response: Resource<ModifyStoreResponse>
    private lateinit var dialogInternet: InternetCheckDialog
    private lateinit var fm: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //   dialog!!.window!!.setBackgroundDrawableResource(R.drawable.corned_white_purple)

        dialogInternet = InternetCheckDialog()
        fm = requireActivity().supportFragmentManager
        return inflater.inflate(R.layout.fragment_position_map, container, false)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.99).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.88).toInt()
        dialog!!.window?.setLayout(width, height)
        dialog!!.window!!.setWindowAnimations(R.style.AnimationsForAddMapDialog)
    }

    override fun onDestroy() {
        super.onDestroy()
        StaticMapClicked.mapIsRunning = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancel.setOnClickListener {
            dismiss()
        }

        store_name.text = nameIn


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_dialog) as SupportMapFragment

        mapFragment.getMapAsync(this)

        confirm_position.setOnClickListener {

            checkInternet()

        }

    }

    private fun addPosition() {
        progress_indicator_position.visibility = View.VISIBLE
        val newStore = store.copy()
        newStore.lat = newPosition.latitude
        newStore.lng = newPosition.longitude

        GlobalScope.launch(Dispatchers.Main) {

            val newStoreJson = jacksonObjectMapper().writeValueAsString(newStore)
            val bodyJson = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                newStoreJson
            )

            response = viewModel.modifyStore(bodyJson)

            if (response.responseCode == 201) {
                store.lat = newPosition.latitude
                store.lng = newPosition.longitude
                dialog!!.dismiss()
            }

            progress_indicator_position.visibility = View.GONE
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        // Add a marker in Market and move the camera
        /*  val marketPosition = LatLng(
              LocationValueListener.myLocation.latitude,
              LocationValueListener.myLocation.longitude
          )

          val location = CameraUpdateFactory.newLatLngZoom(
              marketPosition, 16f
          )
          mMap.animateCamera(location) */

        val marketPosition = LatLng(
            LocationValueListener.myLocation.latitude,
            LocationValueListener.myLocation.longitude
        )

        val markerOptions = MarkerOptions()

        // Setting the position for the marker
        markerOptions.position(marketPosition)
        newPosition = marketPosition


        markerOptions.title(nameIn)

        // Clears the previously touched position
        googleMap.clear()

        // Animating to the touched position
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marketPosition))

        // Placing a marker on the touched position
        googleMap.addMarker(markerOptions)

        val location = CameraUpdateFactory.newLatLngZoom(
            marketPosition, 16f
        )
        mMap.animateCamera(location)

        confirm_position.visibility = View.VISIBLE


        // Setting a click event handler for the map
        /* mMap.setOnMapClickListener { latLng -> // Creating a marker
             val markerOptions = MarkerOptions()

             // Setting the position for the marker
             markerOptions.position(latLng)
             newPosition = latLng


             markerOptions.title(nameIn)

             // Clears the previously touched position
             googleMap.clear()

             // Animating to the touched position
             googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

             // Placing a marker on the touched position
             googleMap.addMarker(markerOptions)


             confirm_position.visibility = View.VISIBLE
         } */
    }


    private fun checkInternet() {
        InternetCheck { internet ->
            if (internet)
                addPosition()
            else {

                //  progress_indicator_dialog.visibility = View.INVISIBLE
                dialogInternet.show(
                    fm,
                    "Internet check"
                )
                fm.executePendingTransactions();

                dialogInternet.dialog!!.setOnCancelListener {
                    checkInternet()
                }


            }
        }
    }
}

