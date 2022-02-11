package com.example.gtm.ui.home.mytask.survey.question

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.response.Question
import com.example.gtm.data.entities.response.QuestionSubCategory
import com.example.gtm.data.entities.ui.Image
import com.example.gtm.databinding.FragmentQuestionBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_question.*

@AndroidEntryPoint
class QuestionFragment : Fragment(), ImageAdapter.ImageItemListener {

    private lateinit var binding: FragmentQuestionBinding
    private lateinit var adapterImage: ImageAdapter
    private val listaImage = ArrayList<Image>()
    private lateinit var choix_image_dialog: ChoixImageDialog
    private lateinit var questionList: ArrayList<Question>
    private var myVar2: String? = ""
    private var scName: String? = ""
    private var i = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionBinding.inflate(inflater, container, false)

        scName = arguments?.getString("scName")

        val myVal = arguments?.getString("questionObject")
        myVar2 = arguments?.getString("quizObject")

        val gson = Gson()
        val objectList = gson.fromJson(myVal, QuestionSubCategory::class.java)

        questionList = objectList.questions as ArrayList<Question>
        Log.i("objectList", "$objectList")




        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().bottom_nav.visibility = View.GONE

        binding.bfTitle.text = scName + ": "

        setupRecycleViewQuestion()


        choix_image_dialog = ChoixImageDialog(
            show_image,
            adapterImage,
            listaImage,
            camera_linear,
            binding.plusImage,
            binding.myPhotoRecycle
        )

        binding.backFromQuiz.setOnClickListener {

            val bundle = bundleOf("quizObject" to myVar2)
            findNavController().navigate(R.id.action_questionFragment_to_categoryFragment, bundle)
        }


        binding.addphoto.setOnClickListener {
            choix_image_dialog.show(requireActivity().supportFragmentManager, "ChoixImage")
        }

        binding.plusImage.setOnClickListener {
            choix_image_dialog.show(requireActivity().supportFragmentManager, "ChoixImage")
        }

        binding.suivant.setOnClickListener {

            if (i < questionList.size) {
                i++
                setQuestion()
            }

        }

        binding.precedent.setOnClickListener {

            if (i > 0) {
                i--
                setQuestion()
            }

        }




        setQuestion()

    }


    private fun setupRecycleViewQuestion() {

        adapterImage = ImageAdapter(this, requireActivity())
        binding.myPhotoRecycle.isMotionEventSplittingEnabled = false
        binding.myPhotoRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.myPhotoRecycle.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.myPhotoRecycle.adapter = adapterImage
        // adapterImage.setItems(listaImage)
    }

    override fun onClickedImage(position: Int) {
        AfficherImageDialog(
            position,
            listaImage,
            camera_linear,
            binding.plusImage,
            adapterImage,
            binding.myPhotoRecycle
        ).show(requireActivity().supportFragmentManager, "afficherimage")
    }


    private fun setQuestion() {
        //Disable suivant or precedent
        if (questionList.size < 2 || i ==0) {
            binding.precedent.visibility = View.GONE
            binding.suivant.visibility = View.VISIBLE
        } else
            if (questionList.size - 1 == i) {
                binding.suivant.visibility = View.GONE
                binding.precedent.visibility = View.VISIBLE

            }


        //Afficher image ou non
        if (questionList[i].images)
            binding.cameraLinear.visibility = View.VISIBLE
        else
            binding.cameraLinear.visibility = View.GONE


        //Image Obligatoire ou non
        if (questionList[i].imagesRequired)
            binding.noteText.text = "Ajouter des photos (Obligatoire)"
        else
            binding.noteText.text = "Ajouter des photos"


        //note obligatoire ou non
        if (questionList[i].required)
            binding.noteText.text = "Donner une note (Obligatoire) :"
        else
            binding.noteText.text = "Donner une note :"

        binding.questionContenu.text = questionList[i].name


    }


}

