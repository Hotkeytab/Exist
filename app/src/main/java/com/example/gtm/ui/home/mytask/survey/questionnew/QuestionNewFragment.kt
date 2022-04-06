package com.example.gtm.ui.home.mytask.survey.questionnew

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.custom.QuestionNewPost
import com.example.gtm.data.entities.custom.UserInf
import com.example.gtm.data.entities.remote.QuestionPost
import com.example.gtm.data.entities.response.Question
import com.example.gtm.data.entities.response.QuestionSubCategory
import com.example.gtm.data.entities.ui.Image
import com.example.gtm.databinding.FragmentKpiGraphBinding
import com.example.gtm.databinding.FragmentQuestionNewBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.survey.question.AfficherImageDialog
import com.example.gtm.ui.home.mytask.survey.question.ChoixImageDialog
import com.example.gtm.ui.home.mytask.survey.question.ImageAdapter
import com.example.gtm.utils.remote.Internet.InternetCheckDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_question.*


@AndroidEntryPoint
class QuestionNewFragment : Fragment(), ImageNewAdapter.ImageItemListener {

    private lateinit var binding: FragmentQuestionNewBinding
    lateinit var sharedPref: SharedPreferences
    private lateinit var dialogInternet: InternetCheckDialog
    private lateinit var fm: FragmentManager
    private var myQuestion: Question? = null
    private var emptyQuestion: Boolean = false
    private lateinit var adapterNewImage: ImageNewAdapter
    private var listaImage: ArrayList<Image>? = ArrayList<Image>()
    private var myVar2: String? = ""
    private var scName: String? = ""
    private var userId = 0
    private var storeId = 0
    private var surveyId = 0
    private var visiteId = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionNewBinding.inflate(inflater, container, false)


        Log.i("myQuestionPlz", "${(activity as DrawerActivity).surveyPostArrayList.size}")

        dialogInternet = InternetCheckDialog()
        fm = requireActivity().supportFragmentManager

        requireActivity().bottom_nav.visibility = View.GONE

        val myVal = arguments?.getString("questionObject")
        myVar2 = arguments?.getString("quizObject")
        emptyQuestion = arguments?.getBoolean("empty")!!

        val gson = Gson()
        myQuestion = gson.fromJson(myVal, Question::class.java)

        scName = arguments?.getString("scName")


        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )

        userId = sharedPref.getInt("id", 0)
        storeId = sharedPref.getInt("storeId", 0)
        surveyId = sharedPref.getInt("surveyId", 0)
        visiteId = sharedPref.getInt("visiteId", 0)

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



        binding.addphoto.setOnClickListener {
            ChoixImageNewDialog(
                show_image,
                adapterNewImage,
                listaImage,
                camera_linear,
                binding.plusImage,
                binding.myPhotoRecycle,
            ).show(requireActivity().supportFragmentManager, "ChoixImageNew")
        }


        binding.plusImage.setOnClickListener {
            ChoixImageNewDialog(
                show_image,
                adapterNewImage,
                listaImage,
                camera_linear,
                binding.plusImage,
                binding.myPhotoRecycle,
            ).show(requireActivity().supportFragmentManager, "ChoixImageNew")
        }


        binding.backFromQuiz.setOnClickListener {
            val bundle = bundleOf("quizObject" to myVar2)
            findNavController().navigate(
                R.id.action_questionNewFragment_to_categoryFragment,
                bundle
            )
        }

        binding.terminer.setOnClickListener {
            finish()
        }
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


        setupRecycleViewQuestion()
        searchForQuestion()
    }


    private fun clearInit() {
        binding.ratingBar.rating = 0f
        binding.editText.setText("")
        binding.editText.hint = "Laisser une remarque..."
        binding.cameraLinear.visibility = View.VISIBLE
        binding.plusImage.visibility = View.GONE
    }


    private fun finish() {


        if (controleSaisie()) {
            val userInf = UserInf(userId, storeId, visiteId, surveyId, myQuestion!!.id)
            val myQusObject = QuestionNewPost(
                myQuestion!!.id.toLong(),
                binding.ratingBar.rating.toLong(),
                binding.editText.text.toString(),
                listaImage
            )
            (activity as DrawerActivity).surveyPostArrayList[userInf] = myQusObject
            val bundle = bundleOf("quizObject" to myVar2)
            findNavController().navigate(
                R.id.action_questionNewFragment_to_categoryFragment,
                bundle
            )

        }

    }


    private fun setupRecycleViewQuestion() {

        adapterNewImage = ImageNewAdapter(this, requireActivity())
        binding.myPhotoRecycle.isMotionEventSplittingEnabled = false
        binding.myPhotoRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.myPhotoRecycle.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.myPhotoRecycle.adapter = adapterNewImage
    }

    override fun onClickedImage(position: Int) {
        AfficherImageNewDialog(
            position,
            listaImage,
            camera_linear,
            binding.plusImage,
            adapterNewImage,
            binding.myPhotoRecycle,
        ).show(requireActivity().supportFragmentManager, "afficherimage")
    }


    private fun controleSaisie(): Boolean {

        //Image Obligatoire ou non
        if (myQuestion!!.imagesRequired && (listaImage == null || listaImage!!.size == 0)) {
            binding.cameraText.setTextColor(Color.RED)
            return false
        } else {
            binding.cameraText.setTextColor(Color.BLACK)
        }


        //note obligatoire ou non
        if (binding.ratingBar.rating == 0f) {
            binding.noteText.setTextColor(Color.RED)
            return false
        } else {
            binding.noteText.setTextColor(Color.BLACK)
        }

        return true
    }


    private fun searchForQuestion() {
        (activity as DrawerActivity).surveyPostArrayList.forEach { (v, w) ->
            if(v.questionId == myQuestion!!.id)
            {
                binding.ratingBar.rating = w.rate!!.toFloat()
                binding.editText.setText(w.description)
                if(w.images != null)
                {
                    if(w.images.size != 0)
                    {
                        listaImage = w.images
                        binding.cameraLinear.visibility = View.GONE
                        binding.plusImage.visibility = View.VISIBLE
                        adapterNewImage.setItems(w.images)
                    }
                }
            }

        }
    }

}