package com.example.gtm.ui.home.mytask.positionmap


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import com.example.gtm.ui.home.mytask.StaticMapClicked
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


@AndroidEntryPoint
class PositionMapDialog(
    latitude: Double, longitude: Double, name: String
) :
    DialogFragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var latIn = latitude
    private var longIn = longitude
    private var nameIn = name

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_position_map, container, false)
    }

    override fun onStart() {
        super.onStart()

        val width = (resources.displayMetrics.widthPixels * 0.99).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.88).toInt()
        dialog!!.window?.setLayout(width, height)
        dialog!!.window!!.setWindowAnimations(R.style.AnimationsForMapDialog)
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

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Market and move the camera
        val marketPosition = LatLng(latIn, longIn)
        mMap.addMarker(MarkerOptions().position(marketPosition).title(nameIn))

        val location = CameraUpdateFactory.newLatLngZoom(
            marketPosition, 13f
        )
        mMap.animateCamera(location)
    }
}

