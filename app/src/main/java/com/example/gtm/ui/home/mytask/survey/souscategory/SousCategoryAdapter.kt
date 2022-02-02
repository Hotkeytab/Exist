package com.example.gtm.ui.home.mytask.survey.souscategory

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.data.entities.response.DataX
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.databinding.ItemCategoryBinding
import com.example.gtm.databinding.ItemQuizBinding
import com.example.gtm.databinding.ItemSousCategoryBinding
import com.example.gtm.databinding.ItemTaskBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog

class SousCategoryAdapter(private val listener: SousCategoryFragment, activity: FragmentActivity) :
    RecyclerView.Adapter<SousCategoryViewHolder>() {


    private val activityIns = activity

    interface SousCategoryItemListener {
        fun onClickedSousCategory(sousCategoryId: Int)
    }

    private val items = ArrayList<Survey>()


    fun setItems(items: ArrayList<Survey>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SousCategoryViewHolder {
        val binding: ItemSousCategoryBinding =
            ItemSousCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SousCategoryViewHolder(binding, listener as SousCategoryItemListener, activityIns)

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SousCategoryViewHolder, position: Int) =
        holder.bind(items[position])
}

class SousCategoryViewHolder(
    private val itemBinding: ItemSousCategoryBinding,
    private val listener: SousCategoryAdapter.SousCategoryItemListener,
    private val activityIns: FragmentActivity
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {


    private lateinit var sousCategoryResponse: Survey


    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: Survey) {
        this.sousCategoryResponse = item

    }


    override fun onClick(v: View?) {
          listener.onClickedSousCategory(sousCategoryResponse.id)
    }


}
