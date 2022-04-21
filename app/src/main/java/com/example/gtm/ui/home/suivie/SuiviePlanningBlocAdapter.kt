package com.example.gtm.ui.home.suivie

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.R
import com.example.gtm.data.entities.response.DataX
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.databinding.ItemBlocSuivieBinding
import com.example.gtm.databinding.ItemTaskBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.StaticMapClicked
import com.example.gtm.ui.home.mytask.daysFilter
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog
import com.example.gtm.ui.home.suivie.detail.SuiviDetailActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_task.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SuiviePlanningBlocAdapter(
    private val listener: SuiviePlanningFragment,
    activity: FragmentActivity,
    activityDrawer2: DrawerActivity,
    listaSurveyResponse2: ArrayList<DataX>
) :
    RecyclerView.Adapter<TaskViewBlocHolder>() {


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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewBlocHolder {
        val binding: ItemBlocSuivieBinding =
            ItemBlocSuivieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewBlocHolder(
            binding,
            listener as TaskItemListener,
            activityIns,
            parent,
            activityDrawer,
            listaSurveyResponse,

            )

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TaskViewBlocHolder, position: Int) =
        holder.bind(items[position])
}

class TaskViewBlocHolder(
    private val itemBinding: ItemBlocSuivieBinding,
    private val listener: SuiviePlanningBlocAdapter.TaskItemListener,
    private val activityIns: FragmentActivity,
    private val parent: ViewGroup,
    private val activityDrawer: DrawerActivity,
    private val listaSurveyResponse: ArrayList<DataX>
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {


    private lateinit var visiteResponse: Visite
    private lateinit var dialog: PositionMapDialog
    lateinit var sharedPref: SharedPreferences
    private var finalDistance = ""

    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: Visite) {
        this.visiteResponse = item



        //Normal Date Format
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(visiteResponse.day)
        format.applyPattern("dd-MM-yyyy")
        val dateformat = format.format(date)
        //Formidable Date Format
        val sdf = SimpleDateFormat("EEEE")
        val sdf2 = SimpleDateFormat("dd MMMM yyyy")
        val sdf3 = SimpleDateFormat("dd-MM-yyyy")
        val dayOfTheWeek = sdf.format(sdf3.parse(dateformat))
        val dayOfTheWeek2 = sdf2.format(sdf3.parse(dateformat))


        val finalDay = "$dayOfTheWeek $dayOfTheWeek2"


        if(!searchForDateInBloc(finalDay)) {
            val layout = itemBinding.thebloc
            layout.orientation = LinearLayout.VERTICAL
            layout.tag = finalDay
            itemBinding.dateText.text = finalDay

            layout.addView(initStore(item))
        }

        else {
            getBloc(finalDay).addView(initStore(item))
        }





    }


    override fun onClick(v: View?) {
        Log.i("Clicked", "${visiteResponse.storeId}")
        putStoreName(visiteResponse.store.name)
        listener.onClickedTask(
            visiteResponse.storeId,
            finalDistance
        )
    }


    private fun initStore(item:Visite) : View
    {
        var suivie = false
        val afterSuiviArray = ArrayList<DataX>()


        val inflater =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task, null)

        if (!item.planned)
            inflater.bar_hors_plan.visibility = View.VISIBLE

        inflater.pointage.visibility = View.VISIBLE


        if (item.start != null) {
            inflater.pointage_entre_circle_green.visibility = View.VISIBLE
            inflater.arrive.visibility = View.VISIBLE
            inflater.arrive.text = "Début : ${testDay(item.start)}"

            inflater.pointage_entre_circle_red.visibility = View.GONE
        } else {
            inflater.pointage_entre_circle_green.visibility = View.GONE
            inflater.arrive.visibility = View.GONE
            inflater.pointage_entre_circle_red.visibility = View.VISIBLE
        }

        if (item.end != null) {
            inflater.pointage_sortie_circle_red.visibility = View.GONE
            inflater.depart.visibility = View.VISIBLE
            inflater.depart.text = "Fin      : ${testDay(item.end)}"
            inflater.pointage_sortie_circle_green.visibility = View.VISIBLE
        } else {
            inflater.pointage_sortie_circle_red.visibility = View.VISIBLE
            inflater.depart.visibility = View.GONE
            inflater.pointage_sortie_circle_green.visibility = View.GONE
        }


        inflater.name.text = item.store.name
        inflater.place.text = item.store.governorate + ", " + item.store.address

        for (i in listaSurveyResponse) {
            if (item.storeId == i.storeId &&  extractDate(item.day) == extractDate(i.createdAt)) {
                suivie = true
                afterSuiviArray.add(i)
            }
        }

        if (suivie) {
            inflater.store_icon_blue.visibility = View.GONE
            inflater.store_icon_red.visibility = View.GONE
            inflater.store_icon_green.visibility = View.VISIBLE
        } else {
            inflater.store_icon_blue.visibility = View.GONE
            inflater.store_icon_green.visibility = View.GONE
            inflater.store_icon_red.visibility = View.VISIBLE
        }

        inflater.show_map.setOnClickListener {

            if (!StaticMapClicked.mapIsRunning) {
                StaticMapClicked.mapIsRunning = true

                PositionMapDialog(
                    item.store.lat,
                    item.store.lng,
                    item.store.name
                ).show(activityIns.supportFragmentManager, "PositionMapDialog")
            }

        }


        if(inflater.store_icon_green.visibility == View.VISIBLE) {
            inflater.store_icon_green.setOnClickListener {
                val bundle  = transformArray(afterSuiviArray)
                putStoreName(item.store.name)
                val intent = Intent(activityIns, SuiviDetailActivity::class.java)
                intent.putExtra("mainobject",bundle)
                activityIns.startActivity(intent)
                activityIns.overridePendingTransition(R.anim.right_to_left_activity,R.anim.left_to_right_activity)
            }

            inflater.store_text.setOnClickListener {
                val bundle  = transformArray(afterSuiviArray)
                putStoreName(item.store.name)
                val intent = Intent(activityIns, SuiviDetailActivity::class.java)
                intent.putExtra("mainobject",bundle)
                activityIns.startActivity(intent)
                activityIns.overridePendingTransition(R.anim.right_to_left_activity,R.anim.left_to_right_activity)
            }
        }



        return inflater
    }

    private fun searchForDateInBloc(tag:String) : Boolean
    {
        val layout = parent.findViewWithTag<LinearLayout>(tag)
        return layout!=null
    }

    private fun getBloc(tag:String) : LinearLayout
    {
        return parent.findViewWithTag<LinearLayout>(tag)
    }

    private fun transformArray(afterSuiviArray: ArrayList<DataX>): String {
        return Gson().toJson(afterSuiviArray)
    }


    //Extract date from string
    private fun extractDate(simpleDate: String) : String{
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(simpleDate)
        format.applyPattern("yyyy-MM-dd")
        val dateformat = format.format(date)
        Log.i("newar", dateformat)
        return dateformat

    }


    //Format Date and show it in item
    private fun showDate( inflatter : View) {
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
            inflatter.date_text.visibility = View.VISIBLE
            inflatter.date_text.text = finalDay
            //}

            // }
        }

    }


    //Put Store Name into shared pref
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


    //Format date from string to date then back from date to string
    private fun testDay(day : String) : String
    { //Normal Date Format
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(day)
        date.hours++
        format.applyPattern("HH:mm:ss")
        return format.format(date)
    }


}
