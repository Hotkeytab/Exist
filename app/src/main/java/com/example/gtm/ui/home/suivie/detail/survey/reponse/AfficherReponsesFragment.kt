package com.example.gtm.ui.home.suivie.detail.survey.reponse

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.response.Question
import com.example.gtm.data.entities.response.QuestionSubCategory
import com.example.gtm.databinding.FragmentAfficherReponsesBinding
import com.example.gtm.ui.home.mytask.survey.question.ImageAdapter
import com.example.gtm.ui.home.suivie.detail.SuiviDetailActivity
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AfficherReponsesFragment : Fragment(), ImageAfficherReponseAdapter.ImageItemListener {

    private lateinit var binding: FragmentAfficherReponsesBinding
    private var myVar2: String? = ""
    private var scName: String? = ""
    private var questionArray = ArrayList<Question>()
    private var imagesArray = ArrayList<String>()
    private var l = 0
    private var leftAnimation: Animation? = null
    private var rightAnimation: Animation? = null
    private lateinit var adapterImage: ImageAfficherReponseAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAfficherReponsesBinding.inflate(inflater, container, false)


        scName = arguments?.getString("scName")

        val myVal = arguments?.getString("questionObject")
        myVar2 = arguments?.getString("quizObject")

        val gson = Gson()
        val objectList = gson.fromJson(myVal, QuestionSubCategory::class.java)
        questionArray = objectList.questions as ArrayList<Question>


        Log.i("questionArray", "$questionArray")

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        leftAnimation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.right_to_left_animation_forquestion
        )

        rightAnimation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.left_to_right_animation_forquestion
        )

        binding.backFromQuiz.setOnClickListener {
            val bundle = bundleOf("quizObject" to myVar2)
            findNavController().navigate(
                R.id.action_afficherReponsesFragment_to_categoryDetailFragment,
                bundle
            )
        }


        setQuestion()

        binding.suivant.setOnClickListener {

            suivantQuestion()

        }

        binding.precedent.setOnClickListener {
            precedentQuestion()

        }


        binding.terminer.setOnClickListener {

            val bundle = bundleOf("quizObject" to myVar2)
            findNavController().navigate(
                R.id.action_afficherReponsesFragment_to_categoryDetailFragment,
                bundle
            )
        }

    }


    private fun suivantQuestion() {
        if (l < questionArray.size) {
            binding.cardviewContent.animation = leftAnimation
            leftAnimation = null
            leftAnimation = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.right_to_left_animation_forquestion
            )
            l++
            setQuestion()
        }

    }

    private fun precedentQuestion() {
        if (l > 0) {
            binding.cardviewContent.animation = rightAnimation
            rightAnimation = null
            rightAnimation = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.left_to_right_animation_forquestion
            )
            l--
            setQuestion()
        }
    }

    private fun setQuestion() {
        //Disable suivant or precedent
        if (l == 0) {
            if (questionArray.size == 1) {
                binding.precedent.visibility = View.GONE
                binding.suivant.visibility = View.GONE
                binding.terminer.visibility = View.VISIBLE
            } else {
                binding.precedent.visibility = View.GONE
                binding.suivant.visibility = View.VISIBLE
                binding.terminer.visibility = View.GONE
            }

        } else
            if (l == questionArray.size - 1) {
                binding.precedent.visibility = View.VISIBLE
                binding.suivant.visibility = View.GONE
                binding.terminer.visibility = View.VISIBLE
            } else {
                binding.precedent.visibility = View.VISIBLE
                binding.suivant.visibility = View.VISIBLE
                binding.terminer.visibility = View.GONE

            }


        //Top Menu Quetion Number
        binding.title.text = "Question ${l + 1}"



        binding.questionContenu.text = questionArray[l].name


        searchForResponse()

    }


    private fun searchForResponse() {


        imagesArray = ArrayList<String>()
        for (i in (activity as SuiviDetailActivity).afterSuiviArray) {
            for (j in i.responses) {
                if (j.questionId == questionArray[l].id) {
                    binding.editText.text = j.description.toString()
                    binding.ratingBar.rating = j.rate
                    for (k in j.responsePictures) {
                        imagesArray.add(k.path)
                    }

                    setupRecycleViewQuestion()

                    return
                }
            }
        }
    }


    private fun setupRecycleViewQuestion() {

        adapterImage = ImageAfficherReponseAdapter(this, requireActivity())
        binding.myPhotoRecycle.isMotionEventSplittingEnabled = false
        binding.myPhotoRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.myPhotoRecycle.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.myPhotoRecycle.adapter = adapterImage
        adapterImage.setItems(imagesArray)
    }
    override fun onClickedImage(position: Int) {
    }

}