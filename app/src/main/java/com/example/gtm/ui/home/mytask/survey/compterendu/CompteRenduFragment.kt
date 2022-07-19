package com.example.gtm.ui.home.mytask.survey.compterendu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.custom.QuestionNewPost
import com.example.gtm.data.entities.custom.UserInf
import com.example.gtm.data.entities.remote.ImagePath
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.compterendu.PostSubject
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.compterendu.PostSubjectCompteRendu
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.compterendu.Subject
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.compterendu.SubjectCompteRendu
import com.example.gtm.data.entities.ui.Image
import com.example.gtm.databinding.FragmentCompteRenduBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.kpi.kpifilter.ProgressUploadDialog
import com.example.gtm.utils.resources.Resource
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CompteRenduFragment : Fragment(), ImageNewAdapterCr.ImageItemListener {

    private lateinit var binding: FragmentCompteRenduBinding
    private val viewModel: CompteRenduViewModel by viewModels()
    private lateinit var responseDataCompteRendu: Resource<SubjectCompteRendu>
    private var listaSubjects = ArrayList<Subject>()
    private var listaSubjectsPost = ArrayList<PostSubject>()

    private val progressUploadDialog = ProgressUploadDialog()
    private var fm: FragmentManager? = null
    private val mapSubject: HashMap<String, Int> = HashMap<String, Int>()
    private var arraySubject = ArrayList<String>()
    private var newArraySubjectNew = ArrayList<String>()

    private lateinit var adapterNewImageCr: ImageNewAdapterCr
    private var listaImageCr: ArrayList<Image>? = ArrayList<Image>()
    private var listaImagePath: ArrayList<ImagePath>? = ArrayList<ImagePath>()
    private var visiteId = 0
    lateinit var sharedPref: SharedPreferences




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )

        visiteId = sharedPref.getInt("visiteId", 0)
        binding = FragmentCompteRenduBinding.inflate(inflater, container, false)

        //Get Store Object with ALl Responses Inside
        val myQuizObject = arguments?.getString("quizObject")
        //Log.d("00meherRendu",myQuizObject.toString())

        //Init childFragmentManager
        fm = childFragmentManager


        //If fragment manager is present
        if (!fm!!.isDestroyed)
            progressUploadDialog.show(fm!!, "ProgressUploadDialogCompteRendu")


        //Get Objet of compte rendu
        getAllSubjects()


        //Setup RecycleView Compte Rendu Images
         setupRecycleViewCompteRenduImage()

        //Give back store Object when back to category fragment
        val bundle = bundleOf("quizObject" to myQuizObject)

           // Log.d("00meher",bundle.toString())
        binding.backFromQuiz.setOnClickListener {

        }


        binding.envoyerQuestionnaireAvecCr.setOnClickListener {
            finish()
            findNavController().navigate(
                R.id.action_compteRenduFragment_to_categoryFragment,
                bundle
            )

        }



        //Subject Editext Listener
        binding.subjectText.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                //if subject doesn't exist in the groupList then add it
                if (!checkForChips(newArraySubjectNew, parent.getItemAtPosition(position).toString())) {
                    addChipSubject(parent.getItemAtPosition(position).toString())
                    newArraySubjectNew.add(parent.getItemAtPosition(position).toString())
                }
            }


        //Add photos to compte rendu
        binding.cameraLinearRc.setOnClickListener {
            ChoixImageNewDialogCr(
                binding.showImageCr,
                adapterNewImageCr,
                listaImageCr,
                binding.cameraLinearRc,
                binding.plusImageRc,
                binding.myPhotoCrRecycle,
            ).show(requireActivity().supportFragmentManager, "ChoixImageNewCR")
        }


        //Add photos to compte rendu
        binding.plusImageRc.setOnClickListener {
            ChoixImageNewDialogCr(
                binding.showImageCr,
                adapterNewImageCr,
                listaImageCr,
                binding.cameraLinearRc,
                binding.plusImageRc,
                binding.myPhotoCrRecycle,
            ).show(requireActivity().supportFragmentManager, "ChoixImageNewCR")
        }


        return binding.root
    }




    //Get Objet of compte rendu
    private fun getAllSubjects() {


        lifecycleScope.launch(Dispatchers.Main) {

            //If Fragment is Added
            if (isAdded) {

                //Get Response Data Object of compte rendu
                responseDataCompteRendu = viewModel.getSubjectReport()

                //If Response Good
                if (responseDataCompteRendu.responseCode == 200 ) {
                    //Extract List of Subjects from response
                   // Log.d("00ena", responseDataCompteRendu.data?.toString().toString())
                    listaSubjects = responseDataCompteRendu.data!!.data as ArrayList<Subject>


                    // fill Post compte rendu Object with selected one


                    //Convert List of Object Subject to Map subjectName -> subjectId

                    getSubListSubject()
                    progressUploadDialog.dismiss()

                    //Repeat getSubjects until we get a good response
                } else {
                    getAllSubjects()
                }
            }

        }
    }

    private fun getSubListSubject() {

        for (i in listaSubjects) {
            mapSubject[i.name] = i.id
            arraySubject.add(i.name)

        }
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, arraySubject)
        binding.subjectText.setAdapter(arrayAdapter)
    }

    //Save Question Locally
    private fun finish() {
        var  postSubject : PostSubject
        postSubject =  PostSubject(0)
           for(i in newArraySubjectNew){

               postSubject.reportSubjectId =  mapSubject.get(i)!!
             //  Log.d("newArraySubjectNew",mapSubject.get(i).toString())

               listaSubjectsPost.add(postSubject)
              // Log.d("mapSubject",listaSubjectsPost.toString())
               postSubject =  PostSubject(0) }





        //If ControleSaisie good then
        if (!binding.compteRendu.text.isEmpty()) {
            val PostSubjectCompteRendu = PostSubjectCompteRendu(visiteId,
                binding.compteRendu.text.toString(),
                listaImagePath!!,
                listaSubjectsPost,
                listaImageCr!!
            )
            (activity as DrawerActivity).SubjectCompteRendu!!.clear()

            (activity as DrawerActivity).SubjectCompteRendu!!.add(PostSubjectCompteRendu)

            Log.d("fromQesToC", PostSubjectCompteRendu.toString())


        }
    }


    //Check if the chip exists in the array
    private fun checkForChips(ourArray: ArrayList<String>, name: String): Boolean {

        for (i in ourArray) {
            if (i == name)
                return true
        }

        return false
    }

    //Add Subject chip to grouplayout
    private fun addChipSubject(name: String) {

        binding.chipGroupSubject.addView(createTagSubject(requireContext(), name, "subject"))

    }




    //Create chip with animation
    private fun createTagSubject(context: Context, chipName: String, tagNow: String): Chip {
        return Chip(context).apply {
            val myView = this
            text = chipName
            tag = tagNow
            setChipBackgroundColorResource(R.color.purpleLogin)
            setCloseIconTintResource(R.color.white)
            isCloseIconVisible = true
            setTextColor(ContextCompat.getColor(context, R.color.white))
            //setTextAppearance(R.style.ChipTextAppearance)
            setOnCloseIconClickListener {


                //Animate chips
                val anim = AlphaAnimation(1f, 0f)
                anim.duration = 250
                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        when (tagNow) {
                            "subject" -> {
                                removeChip(newArraySubjectNew, chipName)
                                binding.chipGroupSubject.removeView(myView)

                            }
                        }

                    }

                    override fun onAnimationStart(animation: Animation?) {}
                })

                it.startAnimation(anim)
            }
        }
    }


    //Remove chip from array
    private fun removeChip(ourArray: ArrayList<String>, name: String) {
        for (i in ourArray) {
            if (i == name) {
                ourArray.remove(i)
                return
            }
        }
    }


    //Setup RecycleView Compte Rendu Images
    private fun setupRecycleViewCompteRenduImage() {

        adapterNewImageCr = ImageNewAdapterCr(this, requireActivity())
        binding.myPhotoCrRecycle.isMotionEventSplittingEnabled = false
        binding.myPhotoCrRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.myPhotoCrRecycle.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.myPhotoCrRecycle.adapter = adapterNewImageCr
    }

    override fun onClickedImage(position: Int) {
        AfficherImageNewCrDialog(
            position,
            listaImageCr,
            binding.cameraLinearRc,
            binding.plusImageRc,
            adapterNewImageCr,
            binding.myPhotoCrRecycle,
        ).show(requireActivity().supportFragmentManager, "afficherimageCR")
    }

}