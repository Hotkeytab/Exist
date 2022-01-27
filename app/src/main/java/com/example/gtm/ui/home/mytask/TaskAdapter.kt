package com.example.gtm.ui.home.mytask

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.data.entities.response.DataX
import com.example.gtm.databinding.ItemTaskBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog

class TaskAdapter(private val listener: TaskFragment, activity: FragmentActivity) :
    RecyclerView.Adapter<TaskViewHolder>() {


    private val activityIns = activity

    interface TaskItemListener {
        fun onClickedTask(taskId: Int)
    }

    private val items = ArrayList<DataX>()


    fun setItems(items: ArrayList<DataX>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding: ItemTaskBinding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding, listener as TaskItemListener, activityIns)

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) =
        holder.bind(items[position])
}

class TaskViewHolder(
    private val itemBinding: ItemTaskBinding,
    private val listener: TaskAdapter.TaskItemListener,
    private val activityIns: FragmentActivity
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {


    private lateinit var visiteResponse: DataX
    private lateinit var dialog: PositionMapDialog

    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: DataX) {
        this.visiteResponse = item


        var clicked = false
        itemBinding.name.text = item.store.name
        itemBinding.place.text = item.store.governorate + ", " + item.store.address

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

            if(!StaticMapClicked.mapIsRunning) {
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
          listener.onClickedTask(visiteResponse.id)
    }


}
