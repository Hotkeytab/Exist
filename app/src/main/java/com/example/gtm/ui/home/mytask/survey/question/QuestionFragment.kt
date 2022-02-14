package com.example.gtm.ui.home.mytask.survey.question

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.gtm.data.entities.response.QuizData
import com.example.gtm.data.entities.ui.Survey


@AndroidEntryPoint
class QuestionFragment : Fragment(), ImageAdapter.ImageItemListener {

    private lateinit var binding: FragmentQuestionBinding
    private lateinit var adapterImage: ImageAdapter
    private var listaImage = HashMap<Int,ArrayList<Image>>()
    private lateinit var choix_image_dialog: ChoixImageDialog
    private lateinit var questionList: ArrayList<Question>
    private var myVar2: String? = ""
    private var scName: String? = ""
    private var i = 0
    private var leftAnimation: Animation? = null
    private var rightAnimation: Animation? = null
    private var listaSurvey = HashMap<Int, Survey>()


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


        leftAnimation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.right_to_left_animation_forquestion
        )



        rightAnimation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.left_to_right_animation_forquestion
        )


        requireActivity().bottom_nav.visibility = View.GONE

        binding.bfTitle.text = scName + ": "

        setupRecycleViewQuestion()


        choix_image_dialog = ChoixImageDialog(
            show_image,
            adapterImage,
            listaImage,
            camera_linear,
            binding.plusImage,
            binding.myPhotoRecycle,
            i
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

            suivantFun()

        }

        binding.precedent.setOnClickListener {
            precedentFun()

        }


        binding.terminer.setOnClickListener {

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
    }

    override fun onClickedImage(position: Int) {
        AfficherImageDialog(
            position,
            listaImage,
            camera_linear,
            binding.plusImage,
            adapterImage,
            binding.myPhotoRecycle,
            i
        ).show(requireActivity().supportFragmentManager, "afficherimage")
    }


    private fun setQuestion() {
        //Disable suivant or precedent
        if (i == 0) {
            if (questionList.size == 1) {
                binding.precedent.visibility = View.GONE
                binding.suivant.visibility = View.GONE
                binding.terminer.visibility = View.VISIBLE
            } else {
                binding.precedent.visibility = View.GONE
                binding.suivant.visibility = View.VISIBLE
                binding.terminer.visibility = View.GONE
            }

        } else
            if (i == questionList.size - 1) {
                binding.precedent.visibility = View.VISIBLE
                binding.suivant.visibility = View.GONE
                binding.terminer.visibility = View.VISIBLE
            } else {
                binding.precedent.visibility = View.VISIBLE
                binding.suivant.visibility = View.VISIBLE
                binding.terminer.visibility = View.GONE

            }
        questionList.size - 1 == i

        //Top Menu Quetion Number
        binding.title.text = "Question ${i + 1}"

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


    private fun suivantFun() {
        if (i < questionList.size) {
            binding.cardviewContent.animation = leftAnimation
            leftAnimation = null
            leftAnimation = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.right_to_left_animation_forquestion
            )

            i++
            setQuestion()
            initQuestion()
            adapterImage.clear()
        }

    }


    private fun precedentFun() {
        if (i > 0) {
            binding.cardviewContent.animation = rightAnimation
            rightAnimation = null
            rightAnimation = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.left_to_right_animation_forquestion
            )

            i--
            setQuestion()
            initQuestion()
            adapterImage.clear()
        }
    }


    private fun initQuestion() {
        binding.editText.setText("")
        binding.editText.hint = "Laisser une remarque..."
        binding.ratingBar.rating = 0f
        binding.cameraLinear.visibility = View.VISIBLE
        binding.plusImage.visibility = View.GONE
    }





}

