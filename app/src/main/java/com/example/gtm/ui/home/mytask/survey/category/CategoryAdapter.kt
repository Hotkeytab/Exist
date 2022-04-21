package com.example.gtm.ui.home.mytask.survey.category

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
import androidx.navigation.fragment.findNavController
import com.example.gtm.R
import com.example.gtm.data.entities.response.QuestionCategory
import com.example.gtm.data.entities.response.QuizData
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.ui.drawer.DrawerActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.item_question_sous_category.view.*
import kotlinx.android.synthetic.main.item_sous_category.view.*
import kotlinx.android.synthetic.main.item_sous_category.view.title_subcateg
import kotlinx.android.synthetic.main.item_sous_category_good.view.*
import kotlinx.android.synthetic.main.item_task.view.*


class CategoryAdapter(
    private val listener: CategoryFragment,
    activity: FragmentActivity,
    myVal: String,
    activityDrawer2: DrawerActivity
) :
    RecyclerView.Adapter<CategoryViewHolder>() {


    private val activityIns = activity
    private val drawerActivity = activityDrawer2
    private val myValIns = myVal

    interface CategoryItemListener {
        fun onClickedCategory(categoryId: Int)
    }

    private val items = ArrayList<QuestionCategory>()


    fun setItems(items: ArrayList<QuestionCategory>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding: ItemCategoryBinding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(
            binding,
            listener as CategoryItemListener,
            activityIns,
            parent,
            myValIns,
            drawerActivity
        )

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) =
        holder.bind(items[position])
}

class CategoryViewHolder(
    private val itemBinding: ItemCategoryBinding,
    private val listener: CategoryAdapter.CategoryItemListener,
    private val activityIns: FragmentActivity,
    private var parent: ViewGroup,
    private var myVal: String,
    private var drawerActivity: DrawerActivity
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    //Test if Category is Open
    var isOpenLinear = false
    //Test if Sub Category is open
    var isOpenLinearSC = false
    var addedValues = false

    private lateinit var categoryResponse: QuestionCategory


    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: QuestionCategory) {
        this.categoryResponse = item

        itemBinding.title.text = item.name

        //Expand Category
        expandColapse()

        //Set Listener on category item , expand
        itemBinding.topCardv.setOnClickListener {
            expandColapse()
        }

    }


    override fun onClick(v: View?) {
        listener.onClickedCategory(categoryResponse.id)


    }


    //Expand Category
    private fun expandColapse() {
        var i = 0


        //If Expanded ?
        if (!isOpenLinear) {

            //Expanded = true
            isOpenLinear = true

            //Prepare Expanded Design
            itemBinding.dropArrow.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            itemBinding.dropArrow.setColorFilter(
                ContextCompat.getColor(
                    parent.context,
                    R.color.purpleLogin
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            );
            itemBinding.testLinear.visibility = View.VISIBLE

            //Get layout to Add SUbCategories to category expanded
            val layout = itemBinding.testLinear
            layout.orientation = LinearLayout.VERTICAL
            var test: HashMap<Int, Survey?>?
            //Test if there are sub categories under categories
            if (!addedValues) {
                for (j in categoryResponse.questionSubCategories) {


                    i++
                    test = drawerActivity.listOfQuestionsPerSc[j.questions[0].questionSubCategoryId]

                    if (test != null && test.size != 0) {
                        val inflater =
                            LayoutInflater.from(parent.context)
                                .inflate(R.layout.item_sous_category_good, null)

                        inflater.id = j.id
                        inflater.title_subcateg.text = j.name





                        inflater.setOnClickListener {

                            val responsJson: String = Gson().toJson(j)

                            val bundle = bundleOf(
                                "questionObject" to responsJson,
                                "quizObject" to myVal,
                                "scName" to j.name
                            )
                            if (!drawerActivity.loading)
                                parent.findNavController()
                                    .navigate(
                                        R.id.action_categoryFragment_to_questionFragment,
                                        bundle
                                    )
                        }
                        layout.addView(inflater)


                    } else {
                        var questionFlag = false
                        val inflater =
                            LayoutInflater.from(parent.context)
                                .inflate(R.layout.item_sous_category, null)


                        inflater.id = j.id
                        inflater.title_subcateg.text = j.name

                        val sizeQuestions = j.questions.size
                        var incrementSizeTest = 0
                        for (k in j.questions.indices) {

                            var inflaterQuestion =
                                LayoutInflater.from(parent.context)
                                    .inflate(R.layout.item_question_sous_category, null)


                            drawerActivity.surveyPostArrayList.forEach { (v, w) ->
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
                            }

                            inflaterQuestion.id = j.questions[k].id
                            inflaterQuestion.title_subcateg_question.text = "${j.questions[k].name}"
                            if (j.questions[k].imagesRequired || j.questions[k].required || j.questions[k].images)
                                inflaterQuestion.etoile_obli.text = "*"

                            if (j.questions[k].state == 1) {

                                inflaterQuestion.title_subcateg_question.setTextColor(Color.RED)
                                inflaterQuestion.isFocusable = true
                                inflaterQuestion.isFocusableInTouchMode = true
                                inflaterQuestion.requestFocus()
                                questionFlag = true
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
                                    "scName" to "${j.name} : Question ${k + 1}"
                                )


                                if (!drawerActivity.loading)
                                    parent.findNavController()
                                        .navigate(
                                            R.id.action_categoryFragment_to_questionNewFragment,
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


                        if (questionFlag) {
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
                            }

                        }

                        if (CategoryFragment.LastSc.lsc == inflater.id) {
                            isOpenLinearSC = true
                            inflater.drop_arrow_sc.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
                            inflater.drop_arrow_sc.setColorFilter(
                                ContextCompat.getColor(
                                    parent.context,
                                    R.color.purpleLogin
                                ), android.graphics.PorterDuff.Mode.MULTIPLY
                            )
                            inflater.questionLinear.visibility = View.VISIBLE
                        }

                        layout.addView(inflater)
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
