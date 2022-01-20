package com.example.gtm.ui.home.mytask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gtm.R
import com.example.gtm.data.entities.room.Task
import com.example.gtm.databinding.ItemTaskBinding

class TaskAdapter(private val listener: TaskFragment) : RecyclerView.Adapter<TaskViewHolder>() {


    interface TaskItemListener {
        fun onClickedTask(taskId: Int)
    }

    private val items = ArrayList<Task>()


    fun setItems(items: ArrayList<Task>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding: ItemTaskBinding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding, listener as TaskItemListener)

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) =
        holder.bind(items[position])
}

class TaskViewHolder(
    private val itemBinding: ItemTaskBinding,
    private val listener: TaskAdapter.TaskItemListener
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {


    private lateinit var task: Task


    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: Task) {
        this.task = item


        itemBinding.name.text = item.name
        itemBinding.place.text = item.place

        when (item.type) {
            1 -> {
                itemBinding.type.setImageResource(R.drawable.round_warning_black_24)
                itemBinding.type.setColorFilter(R.color.yellow)
            }
            2 -> {
                itemBinding.type.setImageResource(R.drawable.round_error_black_24)
                itemBinding.type.setColorFilter(R.color.red)
            }
            3 -> {
                itemBinding.type.setColorFilter(R.color.blue)
            }
        }


    }


    override fun onClick(v: View?) {
        //  listener.onClickedRecommand(formation.idFormation)
    }


}
