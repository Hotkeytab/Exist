package com.example.gtm.ui.home.mytask.survey.question

import android.content.Context
import android.content.SharedPreferences
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
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.utils.Image.UploadRequestBody
import com.example.gtm.utils.extensions.getFileName
import android.provider.MediaStore.Images

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.fragment.app.viewModels
import com.example.gtm.data.entities.remote.ImagePath
import com.example.gtm.data.entities.remote.QuestionPost
import com.example.gtm.data.entities.remote.SurveyPost
import com.example.gtm.data.entities.response.SuccessResponse
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.utils.remote.Internet.ProgressRequestBody
import com.example.gtm.utils.resources.Resource
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*






@AndroidEntryPoint
class QuestionFragment : Fragment(), ImageAdapter.ImageItemListener,
    UploadRequestBody.UploadCallback, ProgressRequestBody.UploadCallbacks {

    private lateinit var binding: FragmentQuestionBinding
    private lateinit var adapterImage: ImageAdapter
    private val viewModel: QuestionFragmentViewModel by viewModels()
    private var listaImage = HashMap<Int, ArrayList<Image>>()
    private var listaSurvey = HashMap<Int, Survey?>()
    private lateinit var questionList: ArrayList<Question>
    private var myVar2: String? = ""
    private var scName: String? = ""
    private var i = 0
    private var leftAnimation: Animation? = null
    private var rightAnimation: Animation? = null
    private lateinit var finalSurvey: SurveyPost
    private var survey: Survey? = null
    lateinit var responseData: Resource<SuccessResponse>
    private var imagePathString = ""
    private var imagePathArrayList: ArrayList<ImagePath?> = ArrayList<ImagePath?>()
    private var listaOnlyImage = ArrayList<Image>()
    private var userId = 0
    private var storeId = 0
    private var surveyId = 0
    lateinit var sharedPref: SharedPreferences


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


        (activity as DrawerActivity).lastTimeClicked = 1
        Log.i("lastTime","${(activity as DrawerActivity).lastTimeClicked}")

        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        userId = sharedPref.getInt("id", 0)
        storeId = sharedPref.getInt("storeId", 0)
        surveyId = sharedPref.getInt("surveyId", 0)

        Log.i("welcome", "$surveyId")
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

        binding.bfTitle.text = "$scName: "

        setupRecycleViewQuestion()


        binding.backFromQuiz.setOnClickListener {

            val bundle = bundleOf("quizObject" to myVar2)
            findNavController().navigate(R.id.action_questionFragment_to_categoryFragment, bundle)
        }


        binding.addphoto.setOnClickListener {
            ChoixImageDialog(
                show_image,
                adapterImage,
                listaImage,
                camera_linear,
                binding.plusImage,
                binding.myPhotoRecycle,
                i
            ).show(requireActivity().supportFragmentManager, "ChoixImage")
        }

        binding.plusImage.setOnClickListener {
            ChoixImageDialog(
                show_image,
                adapterImage,
                listaImage,
                camera_linear,
                binding.plusImage,
                binding.myPhotoRecycle,
                i
            ).show(requireActivity().supportFragmentManager, "ChoixImage")
        }

        binding.suivant.setOnClickListener {

            suivantFun()

        }

        binding.precedent.setOnClickListener {
            precedentFun()

        }


        binding.terminer.setOnClickListener {
            envoyerReponses()
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


        //Top Menu Quetion Number
        binding.title.text = "Question ${i + 1}"

        //Image Obligatoire ou non
        if (questionList[i].imagesRequired)
            binding.cameraText.text = "Ajouter des photos (Obligatoire)"
        else
            binding.cameraText.text = "Ajouter des photos"


        //note obligatoire ou non
        if (questionList[i].required)
            binding.noteText.text = "Donner une note (Obligatoire) :"
        else
            binding.noteText.text = "Donner une note :"

        binding.questionContenu.text = questionList[i].name

    }


    private fun suivantFun() {
        if (controleSaisie()) {
            if (i < questionList.size) {
                binding.cardviewContent.animation = leftAnimation
                leftAnimation = null
                leftAnimation = AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.right_to_left_animation_forquestion
                )
                saveQuestion()
                i++
                initQuestion()
                setQuestion()

            }
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
            saveQuestion()
            i--
            initQuestion()
            setQuestion()
        }
    }


    private fun envoyerReponses() {

        if (controleSaisie()) {
            saveQuestion()
            //getFinalSurvey()
        nextCategory()
        }
    }

    private fun initQuestion() {


        if (listaSurvey[i] != null) {
            binding.ratingBar.rating = listaSurvey[i]!!.rate
            binding.editText.setText(listaSurvey[i]!!.description)

            if (listaSurvey[i]!!.urls != null) {
                if (listaSurvey[i]!!.urls!!.size != 0) {
                    binding.cameraLinear.visibility = View.GONE
                    binding.plusImage.visibility = View.VISIBLE
                    adapterImage.clear()
                    adapterImage.setItems(listaSurvey[i]!!.urls!!)
                } else {
                    binding.cameraLinear.visibility = View.VISIBLE
                    binding.plusImage.visibility = View.GONE
                    adapterImage.clear()

                }


            } else {
                binding.cameraLinear.visibility = View.VISIBLE
                binding.plusImage.visibility = View.GONE
                adapterImage.clear()

            }
        } else {
            clearInit()
        }


    }


    private fun saveQuestion() {
        val idQuestion = questionList[i].id
        if (listaImage[i] != null)
            survey =
                Survey(
                    idQuestion,
                    binding.ratingBar.rating,
                    binding.editText.text.toString(),
                    listaImage[i]!!
                )
        else
            survey =
                Survey(idQuestion, binding.ratingBar.rating, binding.editText.text.toString(), null)


        listaSurvey[i] = survey

    }


    private fun clearInit() {
        binding.ratingBar.rating = 0f
        binding.editText.setText("")
        binding.editText.hint = "Laisser une remarque..."
        binding.cameraLinear.visibility = View.VISIBLE
        binding.plusImage.visibility = View.GONE
        adapterImage.clear()
    }


    private fun convertToFile(
        image: Image,
        images: ArrayList<ImagePath?>?,
        listMultipartBody: ArrayList<MultipartBody.Part?>
    ): MultipartBody.Part? {


        /*    val images: ArrayList<ImagePath?>? = null


            survey!!.urls!!.forEach { i ->

                val selectedImageUri = getImageUri(requireContext(), i.url)

                if (selectedImageUri != null) {
                    val parcelFileDescriptor =
                        requireActivity().contentResolver.openFileDescriptor(selectedImageUri, "r", null)


                    val inputStream = FileInputStream(parcelFileDescriptor!!.fileDescriptor)

                    val file = File(
                        requireActivity().cacheDir,
                        requireActivity().contentResolver.getFileName(selectedImageUri)
                    )

                    images!!.add(ImagePath(file.name))
                    val body = UploadRequestBody(file, "image", this)

                    val outputStream = FileOutputStream(file)
                    inputStream.copyTo(outputStream)

                    MultipartBody.Part.createFormData(
                        file.name, file.name,
                        body
                    )


                }

                Log.i("bassass","$images")
            }*/

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

            val body = ProgressRequestBody(file,"image", this)

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
        }

        return null


    }


    @DelicateCoroutinesApi
    private fun getFinalSurvey() {

        /*  val cf : MultipartBody.Part? = convertToFile(listaImage[i]!![0])
          val cs : String = convertToString(listaImage[i]!![0])!!


          val aa : ArrayList<ImagePath?>  = ArrayList<ImagePath?> ()
          aa.add(ImagePath(cs))


          val bb : ArrayList<MultipartBody.Part?>   = ArrayList<MultipartBody.Part?>  ()
          bb.add(cf)


          val qp = QuestionPost(1,3,"Testa Taha Day and Night3",aa)

          val aap : ArrayList<QuestionPost> = ArrayList<QuestionPost> ()
          aap.add(qp)
          val qp2 = SurveyPost(2,1,1,aap)

          val userNewJson = jacksonObjectMapper().writeValueAsString(qp2)
          val bodyJson = RequestBody.create(
              "application/json; charset=utf-8".toMediaTypeOrNull(),
              userNewJson
          ) */

        val listMultipartBody = ArrayList<MultipartBody.Part?>()
        val listBody = ArrayList<QuestionPost>()

        // reset after callback
        listaSurvey.forEach { (_, v) ->

            val images = ArrayList<ImagePath?>()

            if (v?.urls != null) {
                v.urls.forEach { i ->
                    Log.i("uploaded","BeforeCalled")
                    convertToFile(i, images, listMultipartBody)
                }
            }

            val questionPost = QuestionPost(v!!.id.toLong(), v.rate.toLong(), v.description, images)


            listBody.add(questionPost)

        }

        val qp2 = SurveyPost(userId.toLong(), storeId.toLong(), surveyId.toLong(), listBody)
        Log.i("kamehameha", "$qp2")

        val userNewJson = jacksonObjectMapper().writeValueAsString(qp2)
        val bodyJson = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            userNewJson
        )

        GlobalScope.launch(Dispatchers.Main) {
            responseData = viewModel.postSurveyResponse(
                listMultipartBody,
                bodyJson
            ) as Resource<SuccessResponse>
            Log.i("surveyresponse", "Called")
            Log.i("surveyresponse", "$responseData")
        }

    }



    private fun nextCategory()
    {
        var listDoneScId : ArrayList<Int>? = arguments?.getIntegerArrayList("listDoneScId")
        if(listDoneScId == null)
            listDoneScId = ArrayList<Int>()
        listDoneScId?.add(questionList[0].questionSubCategoryId)
        val bundle = bundleOf("quizObject" to myVar2,"listDoneScId" to listDoneScId)
        Log.i("start0","${listDoneScId[0]}")
        findNavController().navigate(R.id.action_questionFragment_to_categoryFragment,bundle)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    override fun onProgressUpdate(percentage: Int) {
        Log.i("upload","$percentage")
    }

    override fun onError() {
        Log.i("upload","Error")
    }

    override fun onFinish(finished:Boolean) {
        Log.i("finished","$finished")
    }


    private fun controleSaisie(): Boolean {

        //Image Obligatoire ou non
        if (questionList[i].imagesRequired && (listaImage[i] == null || listaImage[i]!!.size == 0)) {
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




}

