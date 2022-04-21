package com.example.gtm.ui.home.suivie.detail.survey.reponse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gtm.R
import com.example.gtm.data.entities.ui.Image
import com.example.gtm.databinding.ItemImageBinding
import com.example.gtm.databinding.ItemImageSuiviBinding


class ImageAfficherReponseAdapter(private val listener: AfficherReponsesFragment, activity: FragmentActivity) :
    RecyclerView.Adapter<ImageViewHolder>() {


    private val activityIns = activity

    interface ImageItemListener {
        fun onClickedImage(position: Int)
    }

    private val items = ArrayList<String>()


    fun setItems(items: ArrayList<String>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        val size: Int = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding: ItemImageSuiviBinding =
            ItemImageSuiviBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding, listener as ImageItemListener, activityIns, parent)

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) =
        holder.bind(items[position],position)
}

class ImageViewHolder(
    private val itemBinding: ItemImageSuiviBinding,
    private val listener: ImageAfficherReponseAdapter.ImageItemListener,
    private val activityIns: FragmentActivity,
    private var parent: ViewGroup
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {



    private lateinit var imageResponse: String


    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: String,position: Int) {
        this.imageResponse = item


        Glide.with(itemBinding.root)
            .load(item)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.chargement)
            .into(itemBinding.myimage)


    }


    override fun onClick(v: View?) {
        listener.onClickedImage(adapterPosition)

    }



}
