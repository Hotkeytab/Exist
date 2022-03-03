package com.example.gtm.ui.home.mytask

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.R
import com.example.gtm.data.entities.response.Survey
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.databinding.ItemTaskBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class TaskAdapter(
    private val listener: TaskFragment,
    activity: FragmentActivity,
    activityDrawer2: DrawerActivity
) :
    RecyclerView.Adapter<TaskViewHolder>() {


    private val activityIns = activity
    private val activityDrawer = activityDrawer2

    interface TaskItemListener {
        fun onClickedTask(taskId: Int, distance: String,visite : Visite)

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
        return TaskViewHolder(
            binding,
            listener as TaskItemListener,
            activityIns,
            parent,
            activityDrawer
        )

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) =
        holder.bind(items[position])
}

class TaskViewHolder(
    private val itemBinding: ItemTaskBinding,
    private val listener: TaskAdapter.TaskItemListener,
    private val activityIns: FragmentActivity,
    private val parent: ViewGroup,
    private val activityDrawer: DrawerActivity
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {


    private lateinit var visiteResponse: Visite
    private lateinit var dialog: PositionMapDialog
    lateinit var sharedPref: SharedPreferences
    private val REQUEST_CODE = 2
    private var finalDistance = ""

    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: Visite) {
        this.visiteResponse = item
        var clicked = false
        itemBinding.pointage.visibility = View.VISIBLE





        if(item.pe == 1 )
        {
            itemBinding.pointageEntreCircleGreen.visibility = View.VISIBLE
            itemBinding.arrive.visibility = View.VISIBLE
            itemBinding.arrive.text = "Arrivée : ${item.pe_time}"
            itemBinding.pointageEntreCircleRed.visibility = View.GONE
        }
        else
        {
            itemBinding.pointageEntreCircleGreen.visibility = View.GONE
            itemBinding.arrive.visibility = View.GONE
            itemBinding.pointageEntreCircleRed.visibility = View.VISIBLE
        }

        if(item.ps == 1)
        {
            itemBinding.pointageSortieCircleRed.visibility = View.GONE
            itemBinding.depart.visibility = View.VISIBLE
            itemBinding.depart.text = "Départ : ${item.ps_time}"
            itemBinding.pointageSortieCircleGreen.visibility = View.VISIBLE
        }
        else
        {
            itemBinding.pointageSortieCircleRed.visibility = View.VISIBLE
            itemBinding.depart.visibility = View.GONE
            itemBinding.pointageSortieCircleGreen.visibility = View.GONE
        }
        showDate()


        itemBinding.name.text = item.store.name
        itemBinding.place.text = item.store.governorate + ", " + item.store.address


        val theDistance = distance(
            LocationValueListener.myLocation.latitude.toFloat(),
            LocationValueListener.myLocation.longitude.toFloat(),
            item.store.lat.toFloat(),
            item.store.lng.toFloat()
        )



        if (theDistance < 1000) {

            finalDistance = theDistance.toInt().toString() + " m"
            itemBinding.cardviewColorEnable.setCardBackgroundColor(Color.rgb(255, 255, 255))
            itemBinding.storeIconBlue.setOnClickListener {
                putStoreName(item.store.name)
                listener.onClickedTask(
                    visiteResponse.id,
                    finalDistance,
                    item
                )
                /*  parent.findNavController()
                      .navigate(R.id.action_taskFragment_to_quizFragment)*/
            }

            itemBinding.storeText.setOnClickListener {
                putStoreName(item.store.name)
                listener.onClickedTask(
                    visiteResponse.id,
                    finalDistance,
                    item
                )
                /* parent.findNavController()
                     .navigate(R.id.action_taskFragment_to_quizFragment)*/
            }
        } else {
            finalDistance = (theDistance.toInt() / 1000).toString() + " km"

            itemBinding.cardviewColorEnable.setCardBackgroundColor(Color.rgb(220, 220, 220))
        }

        itemBinding.distance.visibility = View.VISIBLE
        itemBinding.distance.text = finalDistance




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

        itemBinding.storeIconBlue.setOnClickListener {
            putStoreName(item.store.name)
            listener.onClickedTask(
                visiteResponse.storeId,
                finalDistance,
                item
            )
        }


        itemBinding.storeText.setOnClickListener {
            putStoreName(item.store.name)
            listener.onClickedTask(
                visiteResponse.storeId,
                finalDistance,
                item
            )
        }


    }


    override fun onClick(v: View?) {
        Log.i("Clicked", "${visiteResponse.storeId}")
        putStoreName(visiteResponse.store.name)
        listener.onClickedTask(
            visiteResponse.storeId,
            finalDistance,
            visiteResponse
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


    private fun showDate() {
        //Normal Date Format
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(visiteResponse.day)
        format.applyPattern("dd-MM-yyyy")
        val dateformat = format.format(date)

       // checkForDay(dateformat)

        if(daysFilter.weekFilter == 1 || daysFilter.monthFilter == 1) {
           // activityDrawer.HashMaplistaTasksDate.forEach { (k, v) ->

                // if(visiteResponse.day == k && (v.size == 1 || visiteResponse == v[0])) {

                //Formidable Date Format
                val sdf = SimpleDateFormat("EEEE")
                val sdf2 = SimpleDateFormat("dd MMMM yyyy")
                val sdf3 = SimpleDateFormat("dd-MM-yyyy")
                val dayOfTheWeek = sdf.format(sdf3.parse(dateformat))
                val dayOfTheWeek2 = sdf2.format(sdf3.parse(dateformat))

                val finalDay = "$dayOfTheWeek $dayOfTheWeek2"
                itemBinding.dateText.visibility = View.VISIBLE
                itemBinding.dateText.text = finalDay
                //}

           // }
        }

    }


    private fun checkForDay(day: String): Boolean {

        var test = true

        if (activityDrawer.listOfTriDates.size == 0) {
            activityDrawer.listOfTriDates.add(day)
            Log.i("showaray", "false1")
            return  true
        } else {
            for (i in activityDrawer.listOfTriDates) {
                if (day == i) {
                    test = false
                    Log.i("showaray", "false2")
                    Log.i("showaray", "$day")
                    Log.i("showaray", "$i")
                    Log.i("showaray", "${activityDrawer.listOfTriDates}")
                }
            }
        }
        if (test) {
            Log.i("showaray", "false3")
            activityDrawer.listOfTriDates.add(day)
        }
        return test
    }

    private fun putStoreName(storeName: String) {
        Log.i("storename", storeName)
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
