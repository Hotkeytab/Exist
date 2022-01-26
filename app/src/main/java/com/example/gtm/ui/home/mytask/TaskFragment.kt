package com.example.gtm.ui.home.mytask

import android.app.ProgressDialog.show
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.response.DataX
import com.example.gtm.data.entities.response.VisiteResponse
import com.example.gtm.databinding.FragmentTaskBinding
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog
import com.example.gtm.utils.resources.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



@AndroidEntryPoint
class TaskFragment : Fragment(), TaskAdapter.TaskItemListener {

    private lateinit var binding: FragmentTaskBinding
    private lateinit var adapterTask: TaskAdapter
    private var listaTasks = ArrayList<DataX>()
    lateinit var sharedPref: SharedPreferences
    private lateinit var responseData: Resource<VisiteResponse>
    private val viewModel: MyTaskViewModel by viewModels()
    private var userId = 0
    private lateinit var dateTime: String
    private lateinit var fm: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskBinding.inflate(inflater, container, false)

        fm = requireActivity().supportFragmentManager

        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        userId = sharedPref.getInt("id", 0)

        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateTime = simpleDateFormat.format(calendar.time).toString()

        getVisites()




        return binding.root
    }


    private fun setupRecycleViewPredictionDetail() {

        adapterTask = TaskAdapter(this,requireActivity())
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
       // PositionMapDialog().show(fm,"PositionMapDialog")
    }

    @DelicateCoroutinesApi
    private fun getVisites() {
        GlobalScope.launch(Dispatchers.Main) {

            responseData = viewModel.getVisites(userId.toString(),dateTime,dateTime)

            if(responseData.responseCode == 200)
            {
                listaTasks = responseData.data!!.data as ArrayList<DataX>
                setupRecycleViewPredictionDetail()
            }

        }
    }
}