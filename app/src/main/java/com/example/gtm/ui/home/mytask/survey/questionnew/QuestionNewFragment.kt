package com.example.gtm.ui.home.mytask.survey.questionnew

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.example.gtm.R
import com.example.gtm.data.entities.response.Question
import com.example.gtm.data.entities.response.QuestionSubCategory
import com.example.gtm.databinding.FragmentKpiGraphBinding
import com.example.gtm.databinding.FragmentQuestionNewBinding
import com.example.gtm.ui.home.mytask.survey.question.ChoixImageDialog
import com.example.gtm.ui.home.mytask.survey.question.ImageAdapter
import com.example.gtm.utils.remote.Internet.InternetCheckDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_question.*


@AndroidEntryPoint
class QuestionNewFragment : Fragment() {

    private lateinit var binding: FragmentQuestionNewBinding
    lateinit var sharedPref: SharedPreferences
    private lateinit var dialogInternet: InternetCheckDialog
    private lateinit var fm: FragmentManager
    private var myQuestion: Question? = null
    private var emptyQuestion: Boolean = false
    private lateinit var adapterImage: ImageAdapter
    private var myVar2: String? = ""
    private var scName: String? = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionNewBinding.inflate(inflater, container, false)

        dialogInternet = InternetCheckDialog()
        fm = requireActivity().supportFragmentManager

        requireActivity().bottom_nav.visibility = View.GONE

        val myVal = arguments?.getString("questionObject")
        myVar2 = arguments?.getString("quizObject")
        emptyQuestion = arguments?.getBoolean("empty")!!

        val gson = Gson()
        myQuestion = gson.fromJson(myVal, Question::class.java)

        scName = arguments?.getString("scName")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initQuestion()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val bundle = bundleOf("quizObject" to myVar2)
                    findNavController().navigate(
                        R.id.action_questionNewFragment_to_categoryFragment,
                        bundle
                    )
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


    }


    private fun initQuestion() {
        binding.bfTitle.text = "$scName"
        binding.precedent.visibility = View.GONE
        binding.suivant.visibility = View.GONE
        binding.terminer.visibility = View.VISIBLE

        //Image Obligatoire ou non
        if (myQuestion!!.imagesRequired)
            binding.cameraText.text = "Ajouter des photos (Obligatoire)"
        else
            binding.cameraText.text = "Ajouter des photos"


        //note obligatoire ou non
        if (myQuestion!!.required)
            binding.noteText.text = "Donner une note (Obligatoire) :"
        else
            binding.noteText.text = "Donner une note :"

        binding.questionContenu.text = myQuestion!!.name
    }


    private fun clearInit() {
        binding.ratingBar.rating = 0f
        binding.editText.setText("")
        binding.editText.hint = "Laisser une remarque..."
        binding.cameraLinear.visibility = View.VISIBLE
        binding.plusImage.visibility = View.GONE
    }




}