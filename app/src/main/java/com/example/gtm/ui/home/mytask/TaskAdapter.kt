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
    activityDrawer2: DrawerActivity,
    listaTasks2 : ArrayList<Visite>
) :
    RecyclerView.Adapter<TaskViewHolder>() {


    private val activityIns = activity
    private val activityDrawer = activityDrawer2
    private val listaTask = listaTasks2

    interface TaskItemListener {
        fun onClickedTask(taskId: Int, distance: String, visite: Visite,theDistance: Float)

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
            activityDrawer,
            this,
            listaTask,items
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
    private val activityDrawer: DrawerActivity,
    private val taskAdapter: TaskAdapter,
    private val items : ArrayList<Visite>,
    private val items2: ArrayList<Visite>

) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {


    private lateinit var visiteResponse: Visite
    lateinit var sharedPref: SharedPreferences
    private var finalDistance = ""
    private var theDistance = 0f

    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: Visite) {
        this.visiteResponse = item
        itemBinding.pointage.visibility = View.VISIBLE



        //Set UI If Planned
        if (!item.planned)
            itemBinding.deleteVisite.visibility = View.VISIBLE



        //Update UI If Pointage Start Good
        if (item.start != null) {
            itemBinding.pointageEntreCircleGreen.visibility = View.VISIBLE
            itemBinding.arrive.visibility = View.VISIBLE
            itemBinding.arrive.text = "Début : ${testDay(item.start)}"

            itemBinding.pointageEntreCircleRed.visibility = View.GONE
        } else {
            itemBinding.pointageEntreCircleGreen.visibility = View.GONE
            itemBinding.arrive.visibility = View.GONE
            itemBinding.pointageEntreCircleRed.visibility = View.VISIBLE
        }

        //Update Ui if Pointage End Good
        if (item.end != null) {
            itemBinding.pointageSortieCircleRed.visibility = View.GONE
            itemBinding.depart.visibility = View.VISIBLE
            itemBinding.depart.text = "Fin      : ${testDay(item.end)}"
            itemBinding.pointageSortieCircleGreen.visibility = View.VISIBLE
        } else {
            itemBinding.pointageSortieCircleRed.visibility = View.VISIBLE
            itemBinding.depart.visibility = View.GONE
            itemBinding.pointageSortieCircleGreen.visibility = View.GONE
        }


        //Show Date of Visite
        showDate()


        //Update Visite UI
        itemBinding.name.text = item.store.name
        itemBinding.place.text = item.store.governorate + ", " + item.store.address



        //Calculate Distance
         theDistance = distance(
            LocationValueListener.myLocation.latitude.toFloat(),
            LocationValueListener.myLocation.longitude.toFloat(),
            item.store.lat.toFloat(),
            item.store.lng.toFloat()
        )



        //If Distance Good set Item Clickable
        if (theDistance < 250) {

            finalDistance = theDistance.toInt().toString() + " m"
            itemBinding.cardviewColorEnable.setCardBackgroundColor(Color.rgb(255, 255, 255))
            itemBinding.storeIconBlue.setOnClickListener {
                putStoreName(item.store.name)
                putVisiteId(item.id)
                listener.onClickedTask(
                    visiteResponse.id,
                    finalDistance,
                    item,
                    theDistance
                )
            }

            itemBinding.storeText.setOnClickListener {
                putStoreName(item.store.name)
                putVisiteId(item.id)
                listener.onClickedTask(
                    visiteResponse.id,
                    finalDistance,
                    item,
                    theDistance
                )

            }
        } else {
            finalDistance = (theDistance.toInt() / 1000).toString() + " km"

            itemBinding.cardviewColorEnable.setCardBackgroundColor(Color.rgb(220, 220, 220))
        }

        itemBinding.distance.visibility = View.VISIBLE
        itemBinding.distance.text = finalDistance




        //SHow Map Position Item
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

        //Delete Added Visite
        itemBinding.deleteVisite.setOnClickListener {

            SupprimerVisiteDialog(item.id, adapterPosition,taskAdapter,items,items2)
                .show(activityIns.supportFragmentManager, "SupprimerVisiteDialog")
        }


        itemBinding.storeIconBlue.setOnClickListener {
            putStoreName(item.store.name)
            putVisiteId(item.id)
            listener.onClickedTask(
                visiteResponse.storeId,
                finalDistance,
                item,
                theDistance
            )
        }


        itemBinding.storeText.setOnClickListener {
            putStoreName(item.store.name)
            putVisiteId(item.id)
            listener.onClickedTask(
                visiteResponse.storeId,
                finalDistance,
                item,
                theDistance
            )
        }


    }


    override fun onClick(v: View?) {
        putStoreName(visiteResponse.store.name)
        putVisiteId(visiteResponse.id)
        listener.onClickedTask(
            visiteResponse.storeId,
            finalDistance,
            visiteResponse,
            theDistance
        )



    }


    //calculate Distance
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



    //Add  An Hour to the current date because backend service doesn't provide GMT form
    private fun testDay(day : String) : String
    { //Normal Date Format
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(day)
        date.hours++
        format.applyPattern("HH:mm:ss")
        return format.format(date)
    }

    //Show Date and Update UI
    private fun showDate() {
        //Normal Date Format
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(visiteResponse.day)
        format.applyPattern("dd-MM-yyyy")
        val dateformat = format.format(date)


        if (daysFilter.weekFilter == 1 || daysFilter.monthFilter == 1) {

            //Formidable Date Format
            val sdf = SimpleDateFormat("EEEE")
            val sdf2 = SimpleDateFormat("dd MMMM yyyy")
            val sdf3 = SimpleDateFormat("dd-MM-yyyy")
            val dayOfTheWeek = sdf.format(sdf3.parse(dateformat))
            val dayOfTheWeek2 = sdf2.format(sdf3.parse(dateformat))

            val finalDay = "$dayOfTheWeek $dayOfTheWeek2"
            itemBinding.dateText.visibility = View.VISIBLE
            itemBinding.dateText.text = finalDay
        }

    }


    //Put Store Name in SHaredPref
    private fun putStoreName(storeName: String) {
        sharedPref =
            parent.context.getSharedPreferences(
                R.string.app_name.toString(),
                Context.MODE_PRIVATE
            )!!
        with(sharedPref.edit()) {
            this?.putString("storeName", storeName)
        }?.commit()
    }

    //Put VisitId in sharedPref
    private fun putVisiteId(visiteId: Int) {
        sharedPref =
            parent.context.getSharedPreferences(
                R.string.app_name.toString(),
                Context.MODE_PRIVATE
            )!!
        with(sharedPref.edit()) {
            this?.putInt("visiteId", visiteId)
        }?.commit()
    }


}
