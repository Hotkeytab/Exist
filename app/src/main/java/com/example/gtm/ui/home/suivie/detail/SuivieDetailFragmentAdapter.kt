package com.example.gtm.ui.home.suivie.detail


import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.R
import com.example.gtm.data.entities.response.QuizData
import com.example.gtm.data.entities.response.Survey
import com.example.gtm.databinding.ItemQuizBinding
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog

class SuivieDetailFragmentAdapter(private val listener: SuivieDetailFragment, activity: FragmentActivity) :
    RecyclerView.Adapter<SuivieDetailViewHolder>() {


    private val activityIns = activity

    interface SuivieDetailItemListener {
        fun onClickedQuiz(quiz: Survey,surveyId : Int)
    }

    private val items = ArrayList<Survey>()


    fun setItems(items: ArrayList<Survey>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuivieDetailViewHolder {
        val binding: ItemQuizBinding =
            ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuivieDetailViewHolder(binding, listener as SuivieDetailItemListener, activityIns,parent)

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SuivieDetailViewHolder, position: Int) =
        holder.bind(items[position])
}

class SuivieDetailViewHolder(
    private val itemBinding: ItemQuizBinding,
    private val listener: SuivieDetailFragmentAdapter.SuivieDetailItemListener,
    private val activityIns: FragmentActivity,
    private val parent: ViewGroup
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {


    private lateinit var quizResponse: Survey
    private lateinit var dialog: PositionMapDialog
    lateinit var sharedPref: SharedPreferences

    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: Survey) {
        this.quizResponse = item


        itemBinding.surveyName.text = item.name

    }


    override fun onClick(v: View?) {
          listener.onClickedQuiz(quizResponse,quizResponse.id)
    }




}
