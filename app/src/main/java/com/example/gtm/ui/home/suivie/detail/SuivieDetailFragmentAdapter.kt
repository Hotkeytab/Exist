package com.example.gtm.ui.home.suivie.detail


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SuivieDetailFragmentAdapter(private val listener: SuivieDetailFragment, activity: FragmentActivity,sdActivity2: SuiviDetailActivity) :
    RecyclerView.Adapter<SuivieDetailViewHolder>() {


    private val activityIns = activity
    private val sdActivity = sdActivity2

    interface SuivieDetailItemListener {
        fun onClickedQuiz(quiz: Survey,surveyId : Int)
    }

    private val items = ArrayList<Survey>()


    fun setItems(items: ArrayList<Survey>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        val size: Int = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuivieDetailViewHolder {
        val binding: ItemQuizBinding =
            ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuivieDetailViewHolder(binding, listener as SuivieDetailItemListener, activityIns,parent,sdActivity)

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SuivieDetailViewHolder, position: Int) =
        holder.bind(items[position])
}

class SuivieDetailViewHolder(
    private val itemBinding: ItemQuizBinding,
    private val listener: SuivieDetailFragmentAdapter.SuivieDetailItemListener,
    private val activityIns: FragmentActivity,
    private val parent: ViewGroup,
    private val sdActivity: SuiviDetailActivity
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


        itemBinding.surveyName.text = item.name + "\n" + "note:       " +String.format("%.1f",item.average) + "/10"

        for(i in (sdActivity).afterSuiviArray)
        {
            if(i.survey == item)
            {
                itemBinding.time.text = extractDate(i.responses[0].createdAt)
                return

            }
        }



    }


    override fun onClick(v: View?) {
          listener.onClickedQuiz(quizResponse,quizResponse.id)
    }


    private fun extractDate(simpleDate: String) : String{
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(simpleDate)
        format.applyPattern("dd-MM-yyyy")
        val dateformat = format.format(date)
        Log.i("newar", dateformat)
        return dateformat

    }

}
