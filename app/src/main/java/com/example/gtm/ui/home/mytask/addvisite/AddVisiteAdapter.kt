package com.example.gtm.ui.home.mytask.addvisite

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.gtm.R
import com.example.gtm.data.entities.response.DataXX
import com.example.gtm.data.entities.response.Survey
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.databinding.ItemAddStoreBinding
import com.example.gtm.databinding.ItemTaskBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog
import kotlinx.android.synthetic.main.dialog_edit_profile.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class AddVisiteAdapter(
    private val listener: AddVisteDialog,
) :
    RecyclerView.Adapter<TaskViewHolder>() {


    interface TaskItemListener {
        fun onClickedTask(taskId: Int)

    }

    private val items = ArrayList<DataXX>()


    fun setItems(items: ArrayList<DataXX>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding: ItemAddStoreBinding =
            ItemAddStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(
            binding,
            listener as TaskItemListener,
            parent,
        )

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) =
        holder.bind(items[position])
}

class TaskViewHolder(
    private val itemBinding: ItemAddStoreBinding,
    private val listener: AddVisiteAdapter.TaskItemListener,
    private val parent: ViewGroup,
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {


    private lateinit var visiteResponse: DataXX
    private lateinit var dialog: PositionMapDialog
    lateinit var sharedPref: SharedPreferences
    private val REQUEST_CODE = 2
    private var finalDistance = ""
    private var selected = false

    init {
        itemBinding.root.setOnClickListener(this)
    }


    fun bind(item: DataXX) {
        this.visiteResponse = item


        Glide.with(itemBinding.root)
            .load(item.storePictures[0].path)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(itemBinding.storeimageblackwhite)

        itemBinding.name.text = item.name
        itemBinding.place.text = item.governorate
        itemBinding.distance.text = item.address


    }


    override fun onClick(v: View?) {
        listener.onClickedTask(
            visiteResponse.id,
        )

    }


}
