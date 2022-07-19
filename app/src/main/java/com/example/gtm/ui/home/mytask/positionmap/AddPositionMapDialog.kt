package com.example.gtm.ui.home.mytask.positionmap


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.gtm.R
import com.example.gtm.data.entities.response.mytaskplanning.ajoutervisite.ModifyStoreResponse
import com.example.gtm.data.entities.response.mytaskplanning.ajoutervisite.StoreServiceAjouterVisite
import com.example.gtm.ui.home.mytask.LocationValueListener
import com.example.gtm.ui.home.mytask.StaticMapClicked
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
import kotlinx.android.synthetic.main.fragment_position_map.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody


@AndroidEntryPoint
class AddPositionMapDialog(
    name: String,
    store2: StoreServiceAjouterVisite
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
        //init dialog Internet + fragment manager
        dialogInternet = InternetCheckDialog()
        fm = requireActivity().supportFragmentManager
        return inflater.inflate(R.layout.fragment_position_map, container, false)
    }

    override fun onStart() {
        super.onStart()

        //Set Custom height and width
        val width = (resources.displayMetrics.widthPixels * 0.99).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.88).toInt()
        dialog!!.window?.setLayout(width, height)
        dialog!!.window!!.setWindowAnimations(R.style.AnimationsForAddMapDialog)
    }

    override fun onDestroy() {
        super.onDestroy()
        //Flagged as not running to do some tests after
        StaticMapClicked.mapIsRunning = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //dismiss listener
        cancel.setOnClickListener {
            dismiss()
        }

        //Store name
        store_name.text = nameIn


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_dialog) as SupportMapFragment

        mapFragment.getMapAsync(this)

        confirm_position.setOnClickListener {

            //Check internet if good then add position
            checkInternet()

        }

    }


    //Add new position to store
    private fun addPosition() {
        progress_indicator_position.visibility = View.VISIBLE
        //Create a copy from Object
        val newStore = store.copy()
        newStore.lat = newPosition.latitude
        newStore.lng = newPosition.longitude

        //Launch couroutine
        GlobalScope.launch(Dispatchers.Main) {

            //Convert store Object to Json string
            val newStoreJson = jacksonObjectMapper().writeValueAsString(newStore)
            val bodyJson = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                newStoreJson
            )

            //Get response after modifying store
            response = viewModel.modifyStore(bodyJson)

            //If Response is Good
            if (response.responseCode == 201) {
                store.lat = newPosition.latitude
                store.lng = newPosition.longitude
                dialog!!.dismiss()
            }

            progress_indicator_position.visibility = View.GONE
        }
    }

    //ON GOOGLE MAP READY
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Init marketPosition
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

    }


    //Check Internet Before Adding Position
    private fun checkInternet() {
        InternetCheck { internet ->
            //Position is good
            if (internet)
                addPosition()
            //repeat until position is added
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

