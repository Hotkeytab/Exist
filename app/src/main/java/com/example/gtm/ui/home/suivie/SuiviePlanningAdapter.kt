package com.example.gtm.ui.home.suivie

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.R
import com.example.gtm.data.entities.response.DataX
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.databinding.ItemTaskBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.StaticMapClicked
import com.example.gtm.ui.home.mytask.daysFilter
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog
import com.example.gtm.ui.home.suivie.detail.SuiviDetailActivity
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SuiviePlanningAdapter(
    private val listener: SuiviePlanningFragment,
    activity: FragmentActivity,
    activityDrawer2: DrawerActivity,
    listaSurveyResponse2: ArrayList<DataX>
) :
    RecyclerView.Adapter<TaskViewHolder>() {


    private val activityIns = activity
    private val activityDrawer = activityDrawer2
    private val listaSurveyResponse = listaSurveyResponse2


    interface TaskItemListener {
        fun onClickedTask(taskId: Int, distance: String)

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
            listaSurveyResponse,

            )

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) =
        holder.bind(items[position])
}

class TaskViewHolder(
    private val itemBinding: ItemTaskBinding,
    private val listener: SuiviePlanningAdapter.TaskItemListener,
    private val activityIns: FragmentActivity,
    private val parent: ViewGroup,
    private val activityDrawer: DrawerActivity,
    private val listaSurveyResponse: ArrayList<DataX>
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
        var suivie = false
        val afterSuiviArray = ArrayList<DataX>()


        if (!item.planned)
            itemBinding.barHorsPlan.visibility = View.VISIBLE


        itemBinding.pointage.visibility = View.VISIBLE


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


        showDate()

        Log.i("notresuivie","$suivie")
        itemBinding.name.text = item.store.name
        itemBinding.place.text = item.store.governorate + ", " + item.store.address



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


        Log.i("newar", "${listaSurveyResponse.size}")


        for (i in listaSurveyResponse) {
            if (item.storeId == i.storeId &&  extractDate(item.day) == extractDate(i.createdAt)) {
                suivie = true
                afterSuiviArray.add(i)
            }
        }

        Log.i("notresuivie","$suivie")

        if (suivie) {
            itemBinding.storeIconBlue.visibility = View.GONE
            itemBinding.storeIconRed.visibility = View.GONE
            itemBinding.storeIconGreen.visibility = View.VISIBLE
        } else {
            itemBinding.storeIconBlue.visibility = View.GONE
            itemBinding.storeIconGreen.visibility = View.GONE
            itemBinding.storeIconRed.visibility = View.VISIBLE
        }

        if(itemBinding.storeIconGreen.visibility == View.VISIBLE) {
            itemBinding.storeIconGreen.setOnClickListener {
                val bundle  = transformArray(afterSuiviArray)
                putStoreName(item.store.name)
                val intent = Intent(activityIns, SuiviDetailActivity::class.java)
                intent.putExtra("mainobject",bundle)
                activityIns.startActivity(intent)
                activityIns.overridePendingTransition(R.anim.right_to_left_activity,R.anim.left_to_right_activity)
            }

            itemBinding.storeText.setOnClickListener {
                val bundle  = transformArray(afterSuiviArray)
                putStoreName(item.store.name)
                val intent = Intent(activityIns, SuiviDetailActivity::class.java)
                intent.putExtra("mainobject",bundle)
                activityIns.startActivity(intent)
                activityIns.overridePendingTransition(R.anim.right_to_left_activity,R.anim.left_to_right_activity)
            }
        }


        /* itemBinding.storeText.setOnClickListener {
             putStoreName(item.store.name)
             listener.onClickedTask(
                 visiteResponse.storeId,
                 finalDistance
             )
         } */


    }


    override fun onClick(v: View?) {
        Log.i("Clicked", "${visiteResponse.storeId}")
        putStoreName(visiteResponse.store.name)
        listener.onClickedTask(
            visiteResponse.storeId,
            finalDistance
        )
    }


    private fun transformArray(afterSuiviArray: ArrayList<DataX>): String {
        return Gson().toJson(afterSuiviArray)
    }


    private fun extractDate(simpleDate: String) : String{
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(simpleDate)
        format.applyPattern("yyyy-MM-dd")
        val dateformat = format.format(date)
        Log.i("newar", dateformat)
        return dateformat

    }


    private fun showDate() {
        //Normal Date Format
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(visiteResponse.day)
        format.applyPattern("dd-MM-yyyy")
        val dateformat = format.format(date)

        // checkForDay(dateformat)

        if (daysFilter.weekFilter == 1 || daysFilter.monthFilter == 1) {
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


    private fun testDay(day : String) : String
    { //Normal Date Format
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(day)
        date.hours++
        format.applyPattern("HH:mm:ss")
        return format.format(date)
    }


}
