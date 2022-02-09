package com.example.gtm.ui.home.mytask.survey.question

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.data.entities.ui.Image
import com.example.gtm.databinding.ItemImageBinding


class ImageAdapter(private val listener: QuestionFragment, activity: FragmentActivity) :
    RecyclerView.Adapter<ImageViewHolder>() {


    private val activityIns = activity

    interface ImageItemListener {
        fun onClickedImage(position: Int)
    }

    private val items = ArrayList<Image>()


    fun setItems(items: ArrayList<Image>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding: ItemImageBinding =
            ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding, listener as ImageItemListener, activityIns, parent)

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) =
        holder.bind(items[position],position)
}

class ImageViewHolder(
    private val itemBinding: ItemImageBinding,
    private val listener: ImageAdapter.ImageItemListener,
    private val activityIns: FragmentActivity,
    private var parent: ViewGroup
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {



    private lateinit var imageResponse: Image


    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: Image,position: Int) {
        this.imageResponse = item

        itemBinding.myimage.setImageBitmap(item.url)


    }


    override fun onClick(v: View?) {
        listener.onClickedImage(adapterPosition)



    }



}
