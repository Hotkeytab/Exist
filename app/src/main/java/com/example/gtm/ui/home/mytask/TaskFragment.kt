package com.example.gtm.ui.home.mytask

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.data.entities.room.Task
import com.example.gtm.databinding.FragmentTaskBinding


class TaskFragment : Fragment(),TaskAdapter.TaskItemListener {

    private lateinit var binding: FragmentTaskBinding
    private lateinit var adapterTask: TaskAdapter
    private var listaTasks = ArrayList<Task>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskBinding.inflate(inflater,container,false)

        Log.i("Called","Called")
        addStatTasks()
        setupRecycleViewPredictionDetail()

        return binding.root
    }

    private fun addStatTasks()
    {
        val exampleTask1 = Task(0,0,"Magasin General","Cite Ibn Khaldoun")
        val exampleTask2 = Task(1,0,"Monoprix","EL Manar")
        val exampleTask3 = Task(2,1,"Aziza","Arianna Soghra")
        val exampleTask4 = Task(3,2,"Zen","Manar I")
        val exampleTask5 = Task(4,3,"Hugo Boss","Menzah 5")
        val exampleTask6 = Task(5,4,"Celtia","Centre Ville")

        listaTasks.add(exampleTask1)
        listaTasks.add(exampleTask2)
        listaTasks.add(exampleTask3)
        listaTasks.add(exampleTask4)
        listaTasks.add(exampleTask5)
       // listaTasks.add(exampleTask6)
    }

    private fun setupRecycleViewPredictionDetail()
    {

        adapterTask = TaskAdapter(this)
        binding.taskRecycleview.layoutManager = LinearLayoutManager(requireContext())
        binding.taskRecycleview.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.taskRecycleview.adapter = adapterTask
        adapterTask.setItems(listaTasks)
    }

    override fun onClickedTask(taskId: Int) {
        print("test")
    }
}