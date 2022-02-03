package com.example.gtm.ui.home.mytask.survey.category

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.databinding.ItemCategoryBinding
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.gtm.R


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
        return CategoryViewHolder(binding, listener as CategoryItemListener, activityIns, parent)

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) =
        holder.bind(items[position])
}

class CategoryViewHolder(
    private val itemBinding: ItemCategoryBinding,
    private val listener: CategoryAdapter.CategoryItemListener,
    private val activityIns: FragmentActivity,
    private var parent: ViewGroup
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    var isOpenLinear = false
    var addedValues = false

    private lateinit var categoryResponse: Survey


    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: Survey) {
        this.categoryResponse = item


        itemBinding.topCardv.setOnClickListener {
            expandColapse()
        }

    }


    override fun onClick(v: View?) {
        listener.onClickedCategory(categoryResponse.id)


    }


    private fun expandColapse() {
        var i = 0

        if (!isOpenLinear) {

            isOpenLinear = true
            itemBinding.testLinear.visibility = View.VISIBLE
            val layout = itemBinding.testLinear
            layout.orientation = LinearLayout.VERTICAL

            if (!addedValues) {
                for (j in 0..3) {
                    i++
                    val inflater =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_sous_category, null)

                    inflater.id = j + 1 + i * 4
                    inflater.setOnClickListener {
                        Log.i("buttonlistener", inflater.id.toString())

                        parent.findNavController().navigate(R.id.action_categoryFragment_to_questionFragment)
                    }
                    layout.addView(inflater)
                }
            }
            addedValues = true

            val param = itemBinding.constraintMargin.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0, 0, 0, 50)
            itemBinding.constraintMargin.layoutParams = param

        } else {
            itemBinding.testLinear.visibility = View.GONE
            val param = itemBinding.constraintMargin.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0, 0, 0, 0)
            itemBinding.constraintMargin.layoutParams = param
            isOpenLinear = false
        }
    }

    /*    val btnTag = Button(parent.context)
    btnTag.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.MATCH_PARENT
    )
    btnTag.text = "Button " + (j + 1 + i * 4)
    btnTag.id = j + 1 + i * 4*/


}
