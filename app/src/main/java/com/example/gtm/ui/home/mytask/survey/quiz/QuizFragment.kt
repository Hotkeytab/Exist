package com.example.gtm.ui.home.mytask.survey.quiz

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gtm.R
import com.example.gtm.data.entities.response.Quiz
import com.example.gtm.data.entities.response.QuizData
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.databinding.FragmentQuizBinding
import com.example.gtm.ui.drawer.DrawerActivity
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


@AndroidEntryPoint
class QuizFragment : Fragment(), QuizAdapter.QuizItemListener {

    private lateinit var binding: FragmentQuizBinding
    private lateinit var adapterSurvey: QuizAdapter
    private lateinit var responseDataQuiz: Resource<Quiz>
    private var listaQuiz = ArrayList<QuizData>()
    private val viewModel: MyQuizViewModel by viewModels()
    private var storeName: String? = ""
    lateinit var sharedPref: SharedPreferences
    private lateinit var dialogInternet: InternetCheckDialog
    private lateinit var fm: FragmentManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)


        dialogInternet = InternetCheckDialog()
        fm = requireActivity().supportFragmentManager

        DrawerActivity.trackState.currentOne = "quiz fragment"

        (activity as DrawerActivity).listOfQuestionsPerSc = HashMap<Int, HashMap<Int, Survey?>>()
        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        storeName = sharedPref.getString("storeName", "")

        requireActivity().bottom_nav.visibility = View.GONE
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // This callback will only be called when MyFragment is at least Started.
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_quizFragment_to_taskFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.backFromQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_quizFragment_to_taskFragment)
        }

        binding.title.text = storeName

        checkInternet()


        binding.swiperefreshlayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {

            getVisites()
            swiperefreshlayout.isRefreshing = false
        })

    }


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

    }

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

    @DelicateCoroutinesApi
    private fun getVisites() {
        binding.progressIndicator.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.Main) {


            if (isAdded)
                responseDataQuiz = viewModel.getSurvey()

            if (responseDataQuiz.responseCode == 200) {
                listaQuiz = responseDataQuiz.data!!.data as ArrayList<QuizData>
                setupRecycleViewSurvey()
            }

        }
    }


    private fun checkInternet() {
        InternetCheck { internet ->
            if (internet)
                getVisites()
            else {

                //  progress_indicator_dialog.visibility = View.INVISIBLE
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