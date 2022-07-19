package com.example.gtm.ui.home.suivie.detail.survey.reponse

import android.os.Bundle
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
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.quiz.Question
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.quiz.QuestionSubCategory
import com.example.gtm.data.entities.response.suivieplanning.Response
import com.example.gtm.databinding.FragmentAfficherReponsesBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AfficherReponsesFragment : Fragment(), ImageAfficherReponseAdapter.ImageItemListener {

    private lateinit var binding: FragmentAfficherReponsesBinding
    private var myVar2: String? = ""
    private var scName: String? = ""
    private var resposneInside: String? = ""
    private lateinit var responseObject: Response
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


        //Get response Inside From arguments
        resposneInside = arguments?.getString("responseInside")

        //Get sous category from arguments
        scName = arguments?.getString("scName")

        //Get question Object from arguments
        val myVal = arguments?.getString("questionObject")

        //Get Quiz Object from arguments
        myVar2 = arguments?.getString("quizObject")

        //Convert Json Object to QuestionSubCategory Object
        val gson = Gson()
        val objectList = gson.fromJson(myVal, QuestionSubCategory::class.java)

        //Fetch question Array from Object
        questionArray = objectList.questions as ArrayList<Question>

        //Convert Json String to Response Object
        responseObject = gson.fromJson(resposneInside, Response::class.java)



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Animation
        leftAnimation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.right_to_left_animation_forquestion
        )

        //Animation
        rightAnimation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.left_to_right_animation_forquestion
        )


        initQuestion()

        //Back to previous fragment with object as json string
        binding.backFromQuiz.setOnClickListener {
            val bundle = bundleOf("quizObject" to myVar2)
            findNavController().navigate(
                R.id.action_afficherReponsesFragment_to_categoryDetailFragment,
                bundle
            )
        }


    }


    //Init Selected Question with Default values
    private fun initQuestion() {
        binding.bfTitle.text = "$scName"
        binding.precedent.visibility = View.GONE
        binding.suivant.visibility = View.GONE
        binding.terminer.visibility = View.GONE
        binding.editText.text = responseObject.description
        binding.ratingBar.rating = responseObject.rate
        binding.questionContenu.text = responseObject.question.name

        for(i in responseObject.responsePictures)
         imagesArray.add(i.path)

        //Set RecycleView of Question Images
        setupRecycleViewQuestion()

        adapterImage.setItems(imagesArray)
    }


    override fun onClickedImage(position: Int) {
    }


    //Setup RecycleView Question Images
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
    }




}