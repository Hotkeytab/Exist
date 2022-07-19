package com.example.gtm.ui.home.mytask.survey.quiz


import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.quiz.QuizData
import com.example.gtm.databinding.ItemQuizBinding
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog

class QuizAdapter(private val listener: QuizFragment, activity: FragmentActivity) :
    RecyclerView.Adapter<TaskViewHolder>() {


    private val activityIns = activity

    interface QuizItemListener {
        fun onClickedQuiz(quiz: QuizData, surveyId : Int)
    }

    private val items = ArrayList<QuizData>()


    fun setItems(items: ArrayList<QuizData>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding: ItemQuizBinding =
            ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding, listener as QuizItemListener, activityIns,parent)

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) =
        holder.bind(items[position])
}

class TaskViewHolder(
    private val itemBinding: ItemQuizBinding,
    private val listener: QuizAdapter.QuizItemListener,
    private val activityIns: FragmentActivity,
    private val parent: ViewGroup
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {


    private lateinit var quizResponse: QuizData
    private lateinit var dialog: PositionMapDialog
    lateinit var sharedPref: SharedPreferences

    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: QuizData) {
        this.quizResponse = item


        itemBinding.surveyName.text = item.name

    }


    override fun onClick(v: View?) {
          listener.onClickedQuiz(quizResponse,quizResponse.id)
    }




}
