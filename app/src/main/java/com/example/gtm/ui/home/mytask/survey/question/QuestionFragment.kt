package com.example.gtm.ui.home.mytask.survey.question

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.ui.Image
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.databinding.FragmentQuestionBinding
import com.example.gtm.ui.home.mytask.survey.category.CategoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_question.*
import kotlinx.android.synthetic.main.item_image.*

@AndroidEntryPoint
class QuestionFragment : Fragment() ,ImageAdapter.ImageItemListener{

    private lateinit var binding: FragmentQuestionBinding
    private lateinit var adapterImage: ImageAdapter
    private val listaImage = ArrayList<Image>()
    private lateinit var choix_image_dialog : ChoixImageDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionBinding.inflate(inflater, container, false)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().bottom_nav.visibility = View.GONE


        setupRecycleViewQuestion()


        choix_image_dialog = ChoixImageDialog(show_image,adapterImage,listaImage,camera_linear,binding.plusImage,binding.myPhotoRecycle)

        binding.backFromQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_questionFragment_to_categoryFragment)
        }


        binding.addphoto.setOnClickListener {
            choix_image_dialog.show(requireActivity().supportFragmentManager,"ChoixImage")
        }

        binding.plusImage.setOnClickListener {
            choix_image_dialog.show(requireActivity().supportFragmentManager,"ChoixImage")
        }





    }



    private fun setupRecycleViewQuestion() {

        adapterImage= ImageAdapter(this, requireActivity())
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
        AfficherImageDialog(position,listaImage,camera_linear,binding.plusImage,adapterImage,binding.myPhotoRecycle).show(requireActivity().supportFragmentManager,"afficherimage")
    }


}

