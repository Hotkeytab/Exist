package com.example.gtm.ui.home.mytask.survey.quiz

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gtm.R
import com.example.gtm.data.entities.response.DataX
import com.example.gtm.data.entities.response.Quiz
import com.example.gtm.data.entities.response.QuizData
import com.example.gtm.data.entities.response.SurveyResponse
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.databinding.FragmentQuizBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.MyTaskViewModel
import com.example.gtm.utils.remote.Internet.InternetCheck
import com.example.gtm.utils.remote.Internet.InternetCheckDialog
import com.example.gtm.utils.resources.Resource
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialog_add_visite.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@AndroidEntryPoint
class QuizFragment : Fragment(), QuizAdapter.QuizItemListener {

    private lateinit var binding: FragmentQuizBinding
    private lateinit var adapterSurvey: QuizAdapter
    private lateinit var responseDataQuiz: Resource<Quiz>
    private lateinit var responseData2: Resource<SurveyResponse>
    private var listaSurveyResponse = ArrayList<DataX>()
    private var listaQuiz = ArrayList<QuizData>()
    private val viewModel: MyQuizViewModel by viewModels()
    private val viewModel2: MyTaskViewModel by viewModels()
    private var storeName: String? = ""
    lateinit var sharedPref: SharedPreferences
    private lateinit var dialogInternet: InternetCheckDialog
    private lateinit var fm: FragmentManager
    private var userId = 0
    private lateinit var dateTimeBegin: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)


        //Init Internet dialog
        dialogInternet = InternetCheckDialog()
        fm = requireActivity().supportFragmentManager


        //Init Hashmap of questions per sous category
        (activity as DrawerActivity).listOfQuestionsPerSc = HashMap<Int, HashMap<Int, Survey?>>()


        //Get Data from SHared Pref
        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        storeName = sharedPref.getString("storeName", "")
        userId = sharedPref.getInt("id", 0)

        //Hide Bottom Nav Bar
        requireActivity().bottom_nav.visibility = View.GONE
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_quizFragment_to_taskFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        //Back from QuizFragment to Home
        binding.backFromQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_quizFragment_to_taskFragment)
        }

        //Set Title
        binding.title.text = storeName

        //Check Intrent if Good then Get List of Quiz
        checkInternet()


        //Swipe Down Refresh Page
        binding.swiperefreshlayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getVisites()
            swiperefreshlayout.isRefreshing = false
        })

    }


    //Set Up recycleView Quiz
    private fun setupRecycleViewSurvey() {

        adapterSurvey = QuizAdapter(this, requireActivity())
        binding.quizRecycleview.isMotionEventSplittingEnabled = false
        binding.quizRecycleview.layoutManager = LinearLayoutManager(requireContext())
        binding.quizRecycleview.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.quizRecycleview.adapter = adapterSurvey
        adapterSurvey.setItems(listaQuiz)
        binding.progressIndicator.visibility = View.GONE

        if (listaQuiz.size == 0) {
            binding.noquiz.visibility = View.VISIBLE
        } else
            binding.noquiz.visibility = View.GONE

    }


    //OnClicked Quiz inside RecycleView
    override fun onClickedQuiz(quiz: QuizData, surveyId: Int) {

        val responsJson: String = Gson().toJson(quiz)

        putQuestionName(quiz.name)
        val bundle = bundleOf("quizObject" to responsJson)

        sharedPref =
            requireContext().getSharedPreferences(
                R.string.app_name.toString(),
                Context.MODE_PRIVATE
            )!!
        with(sharedPref.edit()) {
            this?.putInt("surveyId", surveyId)
        }?.commit()

        findNavController().navigate(R.id.action_quizFragment_to_categoryFragment, bundle)

    }


    //Get Quizs
    @DelicateCoroutinesApi
    private fun getVisites() {
        binding.progressIndicator.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.Main) {


            if (isAdded)
                responseDataQuiz = viewModel.getSurvey()

            if (responseDataQuiz.responseCode == 200) {
                listaQuiz = responseDataQuiz.data!!.data as ArrayList<QuizData>

                //Here  there is no service to know if quiz is already answered or not  so , to know if you answered
                //the quiz , I had to get the responses of all quizs and then to check every id in response object
                // to see if the quizId exist or not.
                // It could be much easier and performant if the backend developer did this task in the server
                // if every quiz is answered or not.

                getUsedQuiz()


            }

        }
    }


    private fun searchForQuiz(quizId: Int): Boolean {


        for (i in listaSurveyResponse) {
            if (i.surveyId == quizId)
                return true
        }
        return false
    }


    private fun getUsedQuiz() {
        lifecycleScope.launch(Dispatchers.Main) {

            val d = Date()

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            dateTimeBegin = simpleDateFormat.format(d.time).toString()

            if (isAdded)
                responseData2 =
                    viewModel2.getSurveyResponse(userId.toString(), dateTimeBegin, dateTimeBegin)


            if (responseData2.responseCode == 200) {
                listaSurveyResponse = responseData2.data!!.data as ArrayList<DataX>


                listaQuiz =
                    listaQuiz.filter { list -> !searchForQuiz(list.id) } as ArrayList<QuizData>

                setupRecycleViewSurvey()
            } else {
                checkInternet()
            }
        }
    }

    private fun checkInternet() {
        InternetCheck { internet ->
            if (internet)
                getVisites()
            else {

                dialogInternet.show(
                    fm,
                    "Internet check"
                )
                fm.executePendingTransactions()

                dialogInternet.dialog!!.setOnCancelListener {
                    checkInternet()
                }
            }
        }
    }


    private fun putQuestionName(questionName: String) {
        sharedPref =
            requireContext().getSharedPreferences(
                R.string.app_name.toString(),
                Context.MODE_PRIVATE
            )!!

        with(sharedPref.edit()) {
            this?.putString("questionName", questionName)
        }?.commit()
    }

}