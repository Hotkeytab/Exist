package com.example.gtm.ui.home.suivie.detail.survey

import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.databinding.ItemCategoryBinding
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.example.gtm.R
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.quiz.QuestionCategory
import com.example.gtm.data.entities.response.suivieplanning.Response
import com.example.gtm.ui.home.mytask.survey.category.CategoryFragment
import com.example.gtm.ui.home.suivie.detail.SuiviDetailActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.item_question_sous_category.view.*
import kotlinx.android.synthetic.main.item_sous_category.view.*


class CategoryDetailNewAdapter(
    private val listener: CategoryDetailFragment,
    activity: FragmentActivity,
    myVal: String,
    svdActivity: SuiviDetailActivity
) :
    RecyclerView.Adapter<CategoryDetailNewViewHolder>() {


    private val activityIns = activity
    private val myValIns = myVal
    private val suiviDetailActivity = svdActivity


    interface CategoryDetailNewItemListener {
        fun onClickedCategory(categoryId: Int)
    }

    private val items = ArrayList<QuestionCategory>()


    fun setItems(items: ArrayList<QuestionCategory>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged() }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryDetailNewViewHolder {
        val binding: ItemCategoryBinding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryDetailNewViewHolder(
            binding,
            listener as CategoryDetailNewItemListener,
            activityIns,
            parent,
            myValIns,
            suiviDetailActivity

        )

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CategoryDetailNewViewHolder, position: Int) =
        holder.bind(items[position])
}

class CategoryDetailNewViewHolder(
    private val itemBinding: ItemCategoryBinding,
    private val listener: CategoryDetailNewAdapter.CategoryDetailNewItemListener,
    private val activityIns: FragmentActivity,
    private var parent: ViewGroup,
    private var myVal: String,
    private var suiviDetailActivity: SuiviDetailActivity
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    var isOpenLinear = false
    var addedValues = false
    //Test if Sub Category is open
    var isOpenLinearSC = false

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
                    val sizeQuestions = j.questions.size
                    var incrementSizeTest = 0
                    i++

                    val inflater =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_sous_category, null)

                    inflater.id = j.id
                    inflater.title_subcateg.text = j.name


                    inflater.setOnClickListener {

                        if (!isOpenLinearSC) {

                            isOpenLinearSC = true
                            inflater.drop_arrow_sc.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
                            inflater.drop_arrow_sc.setColorFilter(
                                ContextCompat.getColor(
                                    parent.context,
                                    R.color.purpleLogin
                                ), android.graphics.PorterDuff.Mode.MULTIPLY
                            )
                            inflater.questionLinear.visibility = View.VISIBLE
                        } else {
                            isOpenLinearSC = false
                            inflater.drop_arrow_sc.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                            inflater.drop_arrow_sc.setColorFilter(
                                ContextCompat.getColor(
                                    parent.context,
                                    R.color.purpleLogin
                                ), android.graphics.PorterDuff.Mode.MULTIPLY
                            )
                            inflater.questionLinear.visibility = View.GONE
                        }
                    }

                    layout.addView(inflater)



                    for (k in j.questions.indices) {

                        var inflaterQuestion =
                            LayoutInflater.from(parent.context)
                                .inflate(R.layout.item_question_sous_category, null)


                    /*    suiviDetailActivity.afterSuiviArray.forEach { (v, w) ->
                            if (j.questions[k].id == v.questionId) {
                                inflaterQuestion =
                                    LayoutInflater.from(parent.context)
                                        .inflate(
                                            R.layout.item_question_sous_category_good,
                                            null
                                        )



                                if(w.rate != null)
                                    inflaterQuestion.note_sc.text = "${(w.rate*2).toInt()}/10"


                                incrementSizeTest++
                            }
                        } */

                        var responseInside : Response? = null
                        for(w in suiviDetailActivity.afterSuiviArray)
                        {
                            for(x in w.responses)
                            {
                                if(j.questions[k].id == x.question.id)
                                {
                                    inflaterQuestion =
                                        LayoutInflater.from(parent.context)
                                            .inflate(
                                                R.layout.item_question_sous_category_good,
                                                null
                                            )

                                    responseInside = x
                                    incrementSizeTest++
                                }
                            }
                        }

                        Log.i("responseInside","$responseInside")

                        inflaterQuestion.id = j.questions[k].id
                        inflaterQuestion.title_subcateg_question.text = "${j.questions[k].name}"

                        if (j.questions[k].state == 1) {

                            inflaterQuestion.title_subcateg_question.setTextColor(Color.RED)
                            inflaterQuestion.isFocusable = true
                            inflaterQuestion.isFocusableInTouchMode = true
                            inflaterQuestion.requestFocus()
                          //  questionFlag = true
                        }


                        inflaterQuestion.setOnLongClickListener {
                            val snack = Snackbar.make(
                                parent,
                                "${j.questions[k].name}",
                                Snackbar.LENGTH_LONG
                            ).setBackgroundTint(parent.resources.getColor(R.color.purpleLogin))

                            val view: View = snack.view
                            val params = view.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.CENTER
                            view.layoutParams = params
                            snack.show()

                            return@setOnLongClickListener true

                        }

                        inflaterQuestion.setOnClickListener {
                            CategoryFragment.LastSc.lsc = j.id
                            val myObject = j.questions[k]
                            val responsJson: String = Gson().toJson(myObject)
                            val bundle = bundleOf(
                                "questionObject" to responsJson,
                                "empty" to true,
                                "quizObject" to myVal,
                                "scName" to "${j.name} : Question ${k + 1}",
                                "responseInside" to "${Gson().toJson(responseInside)}"
                            )


                            if(responseInside != null)
                                parent.findNavController()
                                    .navigate(
                                        R.id.action_categoryDetailFragment_to_afficherReponsesFragment,
                                        bundle
                                    )

                        }





                        if (sizeQuestions == incrementSizeTest) {
                            inflater.circle_state.setImageResource(R.drawable.ic_check_cricle)
                            itemBinding.dropArrow.setColorFilter(
                                ContextCompat.getColor(
                                    parent.context,
                                    R.color.green
                                ), android.graphics.PorterDuff.Mode.MULTIPLY
                            )
                        }


                        inflater.questionLinear.addView(inflaterQuestion)
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


}
