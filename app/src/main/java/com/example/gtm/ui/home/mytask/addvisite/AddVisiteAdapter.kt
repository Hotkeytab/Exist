package com.example.gtm.ui.home.mytask.addvisite

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gtm.R
import com.example.gtm.data.entities.response.mytaskplanning.ajoutervisite.StoreServiceAjouterVisite
import com.example.gtm.databinding.ItemAddStoreBinding
import kotlin.collections.ArrayList

class AddVisiteAdapter(
    private val listener: AddVisteDialog,
) :
    RecyclerView.Adapter<TaskViewHolder>() {


    interface TaskItemListener {
        fun onClickedTask(taskId: Int,lat:Double?,lng:Double?,name: String,store: StoreServiceAjouterVisite)

    }

    private val items = ArrayList<StoreServiceAjouterVisite>()


    fun setItems(items: ArrayList<StoreServiceAjouterVisite>) {
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

    //list size
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


    private lateinit var visiteResponse: StoreServiceAjouterVisite
    lateinit var sharedPref: SharedPreferences

    init {
        itemBinding.root.setOnClickListener(this)
    }


    fun bind(item: StoreServiceAjouterVisite) {
        this.visiteResponse = item


        //Store Picture
        if(item.storePictures.isNotEmpty())
        Glide.with(itemBinding.root)
            .load(item.storePictures[0].path)
            .placeholder(R.drawable.outline_storefront_24)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(itemBinding.storeimageblackwhite)

        else
            Glide.with(itemBinding.root)
                .load(R.drawable.logo_exist)
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
            visiteResponse.lat,
            visiteResponse.lng,
            visiteResponse.name,
            visiteResponse

        )

    }


}
