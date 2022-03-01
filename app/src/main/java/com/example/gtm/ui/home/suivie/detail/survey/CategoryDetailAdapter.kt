package com.example.gtm.ui.home.suivie.detail.survey

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.databinding.ItemCategoryBinding
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.gtm.R
import com.example.gtm.data.entities.response.QuestionCategory
import com.example.gtm.data.entities.response.QuizData
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.ui.drawer.DrawerActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.item_sous_category.view.*


class CategoryDetailAdapter(
    private val listener: CategoryDetailFragment,
    activity: FragmentActivity,
    myVal: String
) :
    RecyclerView.Adapter<CategoryDetailViewHolder>() {


    private val activityIns = activity
    private val myValIns = myVal

    interface CategoryDetailItemListener {
        fun onClickedCategory(categoryId: Int)
    }

    private val items = ArrayList<QuestionCategory>()


    fun setItems(items: ArrayList<QuestionCategory>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryDetailViewHolder {
        val binding: ItemCategoryBinding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryDetailViewHolder(
            binding,
            listener as CategoryDetailItemListener,
            activityIns,
            parent,
            myValIns
        )

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CategoryDetailViewHolder, position: Int) =
        holder.bind(items[position])
}

class CategoryDetailViewHolder(
    private val itemBinding: ItemCategoryBinding,
    private val listener: CategoryDetailAdapter.CategoryDetailItemListener,
    private val activityIns: FragmentActivity,
    private var parent: ViewGroup,
    private var myVal: String
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    var isOpenLinear = false
    var addedValues = false

    private lateinit var categoryResponse: QuestionCategory


    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: QuestionCategory) {
        this.categoryResponse = item



        itemBinding.title.text = item.name

        expandColapse()
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
            itemBinding.dropArrow.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            itemBinding.dropArrow.setColorFilter(
                ContextCompat.getColor(
                    parent.context,
                    R.color.purpleLogin
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            )
            itemBinding.testLinear.visibility = View.VISIBLE
            val layout = itemBinding.testLinear
            layout.orientation = LinearLayout.VERTICAL
            if (!addedValues) {

                for (j in categoryResponse.questionSubCategories) {
                    i++

                    val inflater =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_sous_category_good, null)


                    inflater.id = j.id

                    inflater.title_subcateg.text = j.name

                    layout.addView(inflater)

                    inflater.setOnClickListener {
                        val responsJson: String = Gson().toJson(j)

                        val bundle = bundleOf(
                            "questionObject" to responsJson,
                            "quizObject" to myVal,
                            "scName" to j.name
                        )

                        parent.findNavController()
                            .navigate(R.id.action_categoryDetailFragment_to_afficherReponsesFragment,bundle)
                    }
                }
            }
            addedValues = true

            val param = itemBinding.constraintMargin.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0, 0, 0, 50)
            itemBinding.constraintMargin.layoutParams = param

        } else {
            itemBinding.dropArrow.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
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
