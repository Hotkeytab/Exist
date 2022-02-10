package com.example.gtm.ui.home.mytask

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.R
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.databinding.ItemTaskBinding
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class TaskAdapter(private val listener: TaskFragment, activity: FragmentActivity) :
    RecyclerView.Adapter<TaskViewHolder>() {


    private val activityIns = activity

    interface TaskItemListener {
        fun onClickedTask(taskId: Int, latitude: Double, Longitude: Double)
    }

    private val items = ArrayList<Visite>()


    fun setItems(items: ArrayList<Visite>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding: ItemTaskBinding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding, listener as TaskItemListener, activityIns, parent)

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) =
        holder.bind(items[position])
}

class TaskViewHolder(
    private val itemBinding: ItemTaskBinding,
    private val listener: TaskAdapter.TaskItemListener,
    private val activityIns: FragmentActivity,
    private val parent: ViewGroup
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {


    private lateinit var visiteResponse: Visite
    private lateinit var dialog: PositionMapDialog
    lateinit var sharedPref: SharedPreferences

    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: Visite) {
        this.visiteResponse = item

        var finalDistance = ""
        var clicked = false



        itemBinding.name.text = item.store.name
        itemBinding.place.text = item.store.governorate + ", " + item.store.address


        val theDistance = distance(
            locationValueListener.myLocation.latitude.toFloat(),
            locationValueListener.myLocation.longitude.toFloat(),
            item.store.lat.toFloat(),
            item.store.lng.toFloat()
        )



        if (theDistance < 1000) {
            finalDistance = theDistance.toInt().toString() + " m"
            itemBinding.cardviewColorEnable.setCardBackgroundColor(Color.rgb(255, 255, 255))

            itemBinding.storeIcon.setOnClickListener {
                putStoreName(item.store.name)
                parent.findNavController()
                    .navigate(R.id.action_taskFragment_to_quizFragment)
            }

            itemBinding.storeText.setOnClickListener {
                putStoreName(item.store.name)
                parent.findNavController()
                    .navigate(R.id.action_taskFragment_to_quizFragment)
            }
        } else {
            finalDistance = (theDistance.toInt() / 1000).toString() + " km"

            itemBinding.cardviewColorEnable.setCardBackgroundColor(Color.rgb(220, 220, 220))
        }

        itemBinding.distance.text = finalDistance

        /* when (item.type) {
             1 -> {
                 itemBinding.type.setImageResource(R.drawable.yellow_warning)
             }
             2 -> {
                 itemBinding.type.setImageResource(R.drawable.final_red_ic)
             }
             3 -> {
                 itemBinding.type.setImageResource(R.drawable.ic_blue_ex)
             }
         } */





        itemBinding.showMap.setOnClickListener {

            if (!StaticMapClicked.mapIsRunning) {
                StaticMapClicked.mapIsRunning = true

                PositionMapDialog(
                    item.store.lat,
                    item.store.lng,
                    item.store.name
                ).show(activityIns.supportFragmentManager, "PositionMapDialog")
            }

        }


    }


    override fun onClick(v: View?) {
        listener.onClickedTask(
            visiteResponse.id,
            visiteResponse.store.lat,
            visiteResponse.store.lng
        )
    }


    fun distance(lat_a: Float, lng_a: Float, lat_b: Float, lng_b: Float): Float {
        val earthRadius = 3958.75
        val latDiff = Math.toRadians((lat_b - lat_a).toDouble())
        val lngDiff = Math.toRadians((lng_b - lng_a).toDouble())
        val a = sin(latDiff / 2) * sin(latDiff / 2) +
                cos(Math.toRadians(lat_a.toDouble())) * cos(Math.toRadians(lat_b.toDouble())) *
                sin(lngDiff / 2) * sin(lngDiff / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = earthRadius * c
        val meterConversion = 1609
        return (distance * meterConversion.toFloat()).toFloat()
    }


    private fun putStoreName(storeName:String)
    {
        sharedPref =
            parent.context.getSharedPreferences(
                R.string.app_name.toString(),
                Context.MODE_PRIVATE
            )!!
        with(sharedPref.edit()) {
            this?.putString("storeName", storeName)
        }?.commit()
    }


}
