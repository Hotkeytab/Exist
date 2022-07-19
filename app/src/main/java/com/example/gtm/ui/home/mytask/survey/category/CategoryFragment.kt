package com.example.gtm.ui.home.mytask.survey.category

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.remote.ImagePath
import com.example.gtm.data.entities.remote.QuestionPost
import com.example.gtm.data.entities.remote.SurveyPost
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.quiz.QuestionCategory
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.quiz.QuizData
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.category.SuccessResponse
import com.example.gtm.data.entities.ui.Image
import com.example.gtm.databinding.FragmentCategoryBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.survey.question.QuestionFragmentViewModel
import com.example.gtm.utils.extensions.getFileName
import com.example.gtm.utils.remote.Internet.ProgressRequestBody
import com.example.gtm.utils.resources.Resource
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.collections.ArrayList
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.compterendu.PostSubjectCompteRendu
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.compterendu.Subject
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.compterendu.SubjectCompteRendu
import java.util.*
import kotlin.concurrent.schedule


@AndroidEntryPoint
class CategoryFragment : Fragment(), CategoryAdapter.CategoryItemListener,
    ProgressRequestBody.UploadCallbacks {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var adapterCategory: CategoryAdapter
    private var listaCategory = ArrayList<QuestionCategory>()
    lateinit var responseData: Resource<SuccessResponse>
    private val viewModel: QuestionFragmentViewModel by viewModels()
    lateinit var sharedPref: SharedPreferences
    private var questionName: String? = ""
    private var myVal: String? = ""
    private var userId = 0
    private var storeId = 0
    private var surveyId = 0
    private var percent = 0
    private var filesNumber = 0
    private var visiteId = 0
    var recentPercent = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)



        //Get Data From SharedPref
        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        questionName = sharedPref.getString("questionName", "")
        userId = sharedPref.getInt("id", 0)
        storeId = sharedPref.getInt("storeId", 0)
        surveyId = sharedPref.getInt("surveyId", 0)
        visiteId = sharedPref.getInt("visiteId", 0)

        requireActivity().bottom_nav.visibility = View.GONE

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Set Title
        binding.title.text = questionName


        //Override onBack Bottom Button Pressed
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    if (!((activity as DrawerActivity).loading))
                        findNavController().navigate(R.id.action_categoryFragment_to_quizFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        //Top Back Button
        binding.backFromQuiz.setOnClickListener {
            if (!((activity as DrawerActivity).loading))
                findNavController().navigate(R.id.action_categoryFragment_to_quizFragment)
        }
        /*

        binding.AjoutCompteRendu.setOnClickListener {
            if (!((activity as DrawerActivity).loading))
                findNavController().navigate(R.id.action_categoryFragment_to_compteRenduFragment)
        }
       */

        //Get Store Object with ALl Responses Inside
        myVal = arguments?.getString("quizObject")


        //Convert Object from Json to Object
        val gson = Gson()
        val objectList = gson.fromJson(myVal, QuizData::class.java)



        //Get ArrayList From Object
        if (objectList != null)
            listaCategory = objectList.questionCategories as ArrayList<QuestionCategory>


        //Change Button Tag and SetUpRecycleView
        binding.envoyerQuestionnaireButton.tag = "good"
        setupRecycleViewCategory()


        //Send Questions Results
        //Old method without compte rendu
        binding.envoyerQuestionnaireButton.setOnClickListener {

            //Search for Incomplete Obligat Questions
            if (!searchForIncompleteQuestion()) {
                setupRecycleViewCategory()

                val snack = Snackbar.make(
                    requireView(),
                    "Vous avez raté une question obligatoire",
                    Snackbar.LENGTH_LONG
                ).setBackgroundTint(resources.getColor(R.color.red))

                val view: View = snack.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.CENTER
                view.layoutParams = params
                snack.show()

            } else {

                (activity as DrawerActivity).loading = true
                binding.progressBarUpload.visibility = View.VISIBLE
                Timer().schedule(1000) {
                    envoyerQuestionnaire()
                }
            }


        }

        //(activity as DrawerActivity).SubjectCompteRendu!!.add(subjectCompteRendu)
       // Log.d("drawer",(activity as DrawerActivity).SubjectCompteRendu!!.get(0).toString())


        //New Method with compte rendu included
        val bundle = bundleOf("quizObject" to myVal)
        binding.AjoutCompteRendu.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_compteRenduFragment,bundle)
        }



    }


    //Set Up RecycleView for Questions

    private fun setupRecycleViewCategory() {

        adapterCategory =
            CategoryAdapter(this, requireActivity(), myVal!!, (activity as DrawerActivity))
        binding.categoryRecycleview.isMotionEventSplittingEnabled = false
        binding.categoryRecycleview.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryRecycleview.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.categoryRecycleview.adapter = adapterCategory
        adapterCategory.setItems(listaCategory)
    }

    override fun onClickedCategory(categoryId: Int) {
    }


    //Search for Incomplete Oblig QUestions
    private fun searchForIncompleteQuestion(): Boolean {
        for (i in listaCategory) {
            for (j in i.questionSubCategories) {
                for (k in j.questions) {
                    if (k.required || k.images || k.imagesRequired) {
                        var test = false
                        (activity as DrawerActivity).surveyPostArrayList.forEach { (v, _) ->
                            if (k.id == v.questionId) {
                                test = true
                            }
                        }
                        if (!test) {
                            k.state = 1
                            return false
                        }
                    }
                }
            }
        }

        return true
    }


    //Send Final REsponses Service
    @SuppressLint("ResourceAsColor")
    private fun envoyerQuestionnaire() {


        var coefTotal = 0.0
        var average = 0.0

        val listMultipartBody = ArrayList<MultipartBody.Part?>()
        val listBody = ArrayList<QuestionPost?>()


        for (i in listaCategory) {
            for (j in i.questionSubCategories) {
                for (k in j.questions) {
                    var questionPostLast: QuestionPost? = null
                    var rateTest = 0.0
                    coefTotal += k.coef

                    (activity as DrawerActivity).surveyPostArrayList.forEach { (v, w) ->

                        if (k.id == v.questionId) {
                            if (w.rate != null)
                                rateTest = w.rate.toDouble() * 2
                            else rateTest = 0.0

                            val images = ArrayList<ImagePath?>()

                            if (w.images != null) {
                                w.images.forEach { i ->
                                    convertToFile(i, images, listMultipartBody)
                                }
                            }

                            questionPostLast =
                                QuestionPost(w.questionId, w.rate, w.description, images)

                        }
                    }

                    listBody.add(questionPostLast)

                    average += k.coef * rateTest

                }
            }
        }
        val imagesRendu = ArrayList<ImagePath?>()
        val imagesRendu1 = ArrayList<Image?>()


        var reduLast: PostSubjectCompteRendu? =  PostSubjectCompteRendu()

        if(!((activity as DrawerActivity).SubjectCompteRendu!!.isEmpty())){


            for ( renduX in (activity as DrawerActivity).SubjectCompteRendu!!){

                for(i in renduX.Images!!){
                    convertToFileRendu(i, imagesRendu, listMultipartBody)}

            }



        }












        val finalAverageBefore: Double = (average / coefTotal)
        val stringDecimal = (finalAverageBefore.toString()).substring(0, 3)

         // var rendu= SubjectCompteRendu()

        val qp2 =
            SurveyPost(
                userId.toLong(),
                storeId.toLong(),
                visiteId,
                surveyId.toLong(),
                stringDecimal.toDouble(),
                listBody,


            )
        Log.d("q2",qp2.toString())



        val userNewJson = jacksonObjectMapper().writeValueAsString(qp2)
        val bodyJson = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            userNewJson
        )

        val userNewJson1 = jacksonObjectMapper().writeValueAsString(reduLast)
        val bodyJson1 = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            userNewJson1
        )

        filesNumber = listMultipartBody.size
        binding.progressUpload.max = filesNumber * 2


        GlobalScope.launch(Dispatchers.Main) {
            responseData = viewModel.postSurveyResponse(
                listMultipartBody,
                bodyJson,
                bodyJson1
            ) as Resource<SuccessResponse>
            Log.d("fromQesToC",userNewJson1.toString())

            if (responseData.responseCode == 201) {
                val snack = Snackbar.make(
                    requireView(),
                    "Questionnaire Envoyé avec succès",
                    Snackbar.LENGTH_LONG
                ).setBackgroundTint(R.color.purpleLogin)
                val view: View = snack.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.BOTTOM
                view.layoutParams = params
                binding.progressBarUpload.visibility = View.GONE
                (activity as DrawerActivity).loading = false
                findNavController().navigate(R.id.action_categoryFragment_to_quizFragment)
                snack.show()
            } else {
                val snack =
                    Snackbar.make(requireView(), "Une Erreur s'est produite", Snackbar.LENGTH_LONG)

                val view: View = snack.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.BOTTOM
                view.layoutParams = params
                snack.show()
                binding.progressBarUpload.visibility = View.GONE
                (activity as DrawerActivity).loading = false
            }
        }

        (activity as DrawerActivity).SubjectCompteRendu!!.clear()
    }


    //Convert ALl Bmp Images to files
    private fun convertToFile(
        image: Image,
        images: ArrayList<ImagePath?>?,
        listMultipartBody: ArrayList<MultipartBody.Part?>
    ): MultipartBody.Part? {

        val selectedImageUri = getImageUri(requireContext(), image.url)

        if (selectedImageUri != null) {
            val parcelFileDescriptor =
                requireActivity().contentResolver.openFileDescriptor(selectedImageUri, "r", null)
                    ?: return null

            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)

            val file = File(
                requireActivity().cacheDir,
                requireActivity().contentResolver.getFileName(selectedImageUri)
            )

            val body = ProgressRequestBody(file, "image", this)


            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)


            images!!.add(ImagePath(file.name))

            val mbp = MultipartBody.Part.createFormData(
                file.name, file.name,
                body
            )

            listMultipartBody.add(mbp)

            requireActivity().contentResolver.delete(selectedImageUri, null, null)

        }

        return null


    }
    private fun convertToFileRendu(
        image: Image,
        images: ArrayList<ImagePath?>?,
        listMultipartBody: ArrayList<MultipartBody.Part?>
    ): MultipartBody.Part? {

        val selectedImageUri = getImageUri(requireContext(), image.url)

        if (selectedImageUri != null) {
            val parcelFileDescriptor =
                requireActivity().contentResolver.openFileDescriptor(selectedImageUri, "r", null)
                    ?: return null

            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)

            val file = File(
                requireActivity().cacheDir,
                requireActivity().contentResolver.getFileName(selectedImageUri)
            )

            val body = ProgressRequestBody(file, "image", this)


            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)


            images!!.add(ImagePath(file.name))

            val mbp = MultipartBody.Part.createFormData(
                "reportPicture", "reportPicture",
                body
            )

            listMultipartBody.add(mbp)

            requireActivity().contentResolver.delete(selectedImageUri, null, null)

        }

        return null


    }



    //GetImageUri from Images
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }


    //Show Progress Update %
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onProgressUpdate(percentage: Int) {


        if (percentage < recentPercent) {
            percent++
            if ((((percent.toFloat() / (filesNumber * 2)) * 100).toInt()) <= 100) {
                binding.textPercentage.text =
                    (((percent.toFloat() / (filesNumber * 2)) * 100).toInt()).toString() + "%"
            }
            binding.progressUpload.setProgress(percent, true)
            recentPercent = 0
        } else {
            recentPercent = percentage
        }


    }

    override fun onError() {
    }

    override fun onFinish(finished: Boolean) {
    }



    object LastSc {
        var lsc = 0
    }

}