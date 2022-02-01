package com.example.gtm.ui.home.mytask.survey.quiz

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.data.entities.response.DataX
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.databinding.ItemQuizBinding
import com.example.gtm.databinding.ItemTaskBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog

class QuizAdapter(private val listener: QuizFragment, activity: FragmentActivity) :
    RecyclerView.Adapter<TaskViewHolder>() {


    private val activityIns = activity

    interface QuizItemListener {
        fun onClickedQuiz(quizId: Int)
    }

    private val items = ArrayList<Survey>()


    fun setItems(items: ArrayList<Survey>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding: ItemQuizBinding =
            ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding, listener as QuizItemListener, activityIns)

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) =
        holder.bind(items[position])
}

class TaskViewHolder(
    private val itemBinding: ItemQuizBinding,
    private val listener: QuizAdapter.QuizItemListener,
    private val activityIns: FragmentActivity
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {


    private lateinit var quizResponse: Survey
    private lateinit var dialog: PositionMapDialog

    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: Survey) {
        this.quizResponse = item

    }


    override fun onClick(v: View?) {
          listener.onClickedQuiz(quizResponse.id)
    }


}
