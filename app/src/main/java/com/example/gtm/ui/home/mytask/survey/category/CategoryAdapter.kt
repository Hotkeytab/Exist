package com.example.gtm.ui.home.mytask.survey.category

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.data.entities.response.DataX
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.databinding.ItemCategoryBinding
import com.example.gtm.databinding.ItemQuizBinding
import com.example.gtm.databinding.ItemTaskBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog
import android.widget.LinearLayout


class CategoryAdapter(private val listener: CategoryFragment, activity: FragmentActivity) :
    RecyclerView.Adapter<CategoryViewHolder>() {


    private val activityIns = activity

    interface CategoryItemListener {
        fun onClickedCategory(categoryId: Int)
    }

    private val items = ArrayList<Survey>()


    fun setItems(items: ArrayList<Survey>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding: ItemCategoryBinding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding, listener as CategoryItemListener, activityIns)

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) =
        holder.bind(items[position])
}

class CategoryViewHolder(
    private val itemBinding: ItemCategoryBinding,
    private val listener: CategoryAdapter.CategoryItemListener,
    private val activityIns: FragmentActivity
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {


    private lateinit var categoryResponse: Survey


    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: Survey) {
        this.categoryResponse = item


     /*   val layout = itemBinding.testLinear
        layout.orientation = LinearLayout.VERTICAL

            for (j in 0..3) {
                val btnTag = Button(this)
                btnTag.setLayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                )
                btnTag.setText("Button " + (j + 1 + i * 4))
                btnTag.setId(j + 1 + i * 4)
                row.addView(btnTag)
            }
            layout.addView(row) */


    }


    override fun onClick(v: View?) {
        listener.onClickedCategory(categoryResponse.id)
    }


}
