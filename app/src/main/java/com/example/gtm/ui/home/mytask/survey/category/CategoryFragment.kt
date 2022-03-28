package com.example.gtm.ui.home.mytask.survey.category

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.remote.ImagePath
import com.example.gtm.data.entities.remote.QuestionPost
import com.example.gtm.data.entities.remote.SurveyPost
import com.example.gtm.data.entities.response.QuestionCategory
import com.example.gtm.data.entities.response.Quiz
import com.example.gtm.data.entities.response.QuizData
import com.example.gtm.data.entities.response.SuccessResponse
import com.example.gtm.data.entities.ui.Image
import com.example.gtm.data.entities.ui.Survey
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.schedule
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import java.text.DecimalFormat


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
        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        questionName = sharedPref.getString("questionName", "")
        userId = sharedPref.getInt("id", 0)
        storeId = sharedPref.getInt("storeId", 0)
        surveyId = sharedPref.getInt("surveyId", 0)
        visiteId = sharedPref.getInt("visiteId",0)

        requireActivity().bottom_nav.visibility = View.GONE

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.title.text = questionName


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    if (!((activity as DrawerActivity).loading))
                        findNavController().navigate(R.id.action_categoryFragment_to_quizFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.backFromQuiz.setOnClickListener {
            if (!((activity as DrawerActivity).loading))
                findNavController().navigate(R.id.action_categoryFragment_to_quizFragment)
        }

        myVal = arguments?.getString("quizObject")


        val gson = Gson()
        val objectList = gson.fromJson(myVal, QuizData::class.java)


        if (objectList != null)
            listaCategory = objectList.questionCategories as ArrayList<QuestionCategory>


        binding.envoyerQuestionnaireButton.tag = "good"
        setupRecycleViewCategory()

        binding.envoyerQuestionnaireButton.setOnClickListener {

            if (binding.envoyerQuestionnaireButton.tag != "bad") {

                (activity as DrawerActivity).loading = true
                binding.progressBarUpload.visibility = View.VISIBLE
                Timer().schedule(1000) {
                    envoyerQuestionnaire()
                }
            }
        }

        // val objectList = gson.fromJson(json, Array<SomeObject>::class.java).asList()
    }


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
        //  findNavController().navigate(R.id.action_categoryFragment_to_sousCategoryFragment)
    }


    @SuppressLint("ResourceAsColor")
    private fun envoyerQuestionnaire() {


        var coefTotal = 0.0
        var average = 0.0
        val bigList: HashMap<Int, HashMap<Int, Survey?>> =
            (activity as DrawerActivity).listOfQuestionsPerSc
        // Log.i("mamaafrica","${(activity as DrawerActivity).listOfQuestionsPerSc}")

        val listMultipartBody = ArrayList<MultipartBody.Part?>()
        val listBody = ArrayList<QuestionPost>()
        //  filesNumber = bigList

        bigList.forEach { (v, k) ->


            k.forEach { (l, m) ->


                coefTotal += m!!.coef
                average += m.coef * m.rate

                val images = ArrayList<ImagePath?>()

                if (m?.urls != null) {
                    m.urls.forEach { i ->
                        convertToFile(i, images, listMultipartBody)
                    }
                }

                val questionPost =
                    QuestionPost(m!!.id.toLong(), m.rate.toLong(), m.description, images)



                listBody.add(questionPost)
            }

        }


        val finalAverageBefore : Double = (average / coefTotal)
        val stringDecimal = (finalAverageBefore.toString()).substring(0,3)




        val qp2 =
            SurveyPost(userId.toLong(), storeId.toLong(),visiteId, surveyId.toLong(), stringDecimal.toDouble(), listBody)


        val userNewJson = jacksonObjectMapper().writeValueAsString(qp2)
        val bodyJson = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            userNewJson
        )

        filesNumber = listMultipartBody.size
        binding.progressUpload.max = filesNumber * 2

        Log.i("filesNumber", "$filesNumber")


        GlobalScope.launch(Dispatchers.Main) {
            responseData = viewModel.postSurveyResponse(
                listMultipartBody,
                bodyJson
            ) as Resource<SuccessResponse>

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
    }


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

            //val body = UploadRequestBody(file, "image", this)

            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

            val newTrim = file.name.trim()

            images!!.add(ImagePath(file.name))

            val mbp = MultipartBody.Part.createFormData(
                file.name, file.name,
                body
            )

            listMultipartBody.add(mbp)

            requireActivity().contentResolver.delete(selectedImageUri,null,null)

        }

        return null


    }


    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    


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


}