package com.example.gtm.ui.drawer.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.gtm.R
import com.example.gtm.data.entities.response.profile.EditProfileResponse
import com.example.gtm.data.entities.ui.User
import com.example.gtm.ui.drawer.DrawerActivityViewModel
import com.example.gtm.utils.Image.UploadRequestBody
import com.example.gtm.utils.extensions.getFileName
import com.example.gtm.utils.extensions.isNumeric
import com.example.gtm.utils.extensions.isValidEmail
import com.example.gtm.utils.extensions.isValidName
import com.example.gtm.utils.remote.Internet.InternetCheck
import com.example.gtm.utils.remote.Internet.InternetCheckDialog
import com.example.gtm.utils.resources.Resource
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_edit_profile.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


@AndroidEntryPoint
class EditProfileDialog(
    user: User,
    picture: String,
    viewModel: DrawerActivityViewModel,
    name: TextView,
    email: TextView,
    phone: TextView,
    lastNameOnly: TextView,
    firstNameONly: TextView,
    profilePicture: ImageView
) :
    DialogFragment(), UploadRequestBody.UploadCallback {

    private val userIn = user
    private val nameIn = name
    private val emailIn = email
    private val phoneIn = phone
    private val lastNameOnlyIn = lastNameOnly
    private val firstNameOnlyIn = firstNameONly
    private var pictureIn = picture
    private val viewModelIn = viewModel
    lateinit var responseDataEditProfile: Resource<EditProfileResponse>
    private var selectedImageUri: Uri? = null
    private var profilePictureIn = profilePicture
    private var file: File? = null
    private var body: UploadRequestBody? = null
    private lateinit var dialogInternet: InternetCheckDialog
    private lateinit var fm: FragmentManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.corned_white_purple)

        //Init dialogInternet
        dialogInternet = InternetCheckDialog()
        return inflater.inflate(R.layout.dialog_edit_profile, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog!!.window!!.setWindowAnimations(R.style.AnimationsForDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Init Fragment Manager
        fm = requireActivity().supportFragmentManager

        //Change profile picture
        profile_picture_dialog.setOnClickListener {
            openImageChoser()
        }
        //Change profile picture
        change_photo_text.setOnClickListener {
            openImageChoser()
        }

        //Change profile after Internet check
        submit.setOnClickListener {
            checkInternet()
        }

        //Prepare Profile And Set All EditTexts
        initProfile()
    }


    //GetUser From shared pref and fill all edittext
    private fun initProfile() {
        firstname_dialog.editText?.setText(userIn.first_name)
        lastname_dialog.editText?.setText(userIn.last_name)
        email_dialog.editText?.setText(userIn.email)
        phone_edit_dialog.editText?.setText(userIn.phone_number)

        Glide.with(this)
            .load(pictureIn)
            .transform(CircleCrop())
            .into(profile_picture_dialog)
    }


    //Change Profile Information
    @DelicateCoroutinesApi
    private fun changeProfile() {

        progress_indicator_dialog.visibility = View.VISIBLE


        //if profile image isn't null prepare the image as a file
        if (selectedImageUri != null) {
            val parcelFileDescriptor =
                requireActivity().contentResolver.openFileDescriptor(selectedImageUri!!, "r", null)
                    ?: return

            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)

            file = File(
                requireActivity().cacheDir,
                requireActivity().contentResolver.getFileName(selectedImageUri!!)
            )

            body = UploadRequestBody(file!!, "image", this)

            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

        }

        //Prepare User Object
        val userNew = User(
            userIn.id,
            firstname_dialog.editText?.text.toString(),
            lastname_dialog.editText?.text.toString(),
            email_dialog.editText?.text.toString(),
            userIn.password,
            phone_edit_dialog.editText?.text.toString(),
            userIn.enabled,
            userIn.gender,
            userIn.roleId
        )

        //Prepare ChangeProfile Coroutine
        GlobalScope.launch(Dispatchers.Main) {

            //Convert User Object to User String Json
            val userNewJson = jacksonObjectMapper().writeValueAsString(userNew)
            val bodyJson = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                userNewJson
            )

            //Test if username and lastname are valid
            if (firstname_dialog.editText?.isValidName() == true && lastname_dialog.editText?.isValidName() == true && email_dialog.editText?.isValidEmail() == true && phone_edit_dialog.editText?.isNumeric() == true) {
                val objectSend: MultipartBody.Part?

                //Test if Object to send is null
                if (file == null || body == null)
                    objectSend = null
                else
                    objectSend = MultipartBody.Part.createFormData(
                        "file", file?.name,
                        body!!
                    )

                //Get Response EditProfile
                responseDataEditProfile =
                    viewModelIn.changeProfile(objectSend, bodyJson) as Resource<EditProfileResponse>


                //If response is good
                if (responseDataEditProfile.responseCode == 201) {

                    //Close Progress Bar
                    progress_indicator_dialog.visibility = View.INVISIBLE

                    //Fill Editexts with response
                    nameIn.text = "${userNew.first_name}  ${userNew.last_name}"
                    emailIn.text = userNew.email
                    phoneIn.text = userNew.phone_number
                    lastNameOnlyIn.text = userNew.last_name
                    firstNameOnlyIn.text = userNew.first_name
                    //Close Dialog
                    dismiss()
                }

            }
        }

    }


    //Choose Image from Gallery
    private fun openImageChoser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
        }
    }

    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 101
    }


    //Load Profile Image onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> {
                    selectedImageUri = data?.data

                    Glide.with(requireActivity())
                        .load(selectedImageUri)
                        .transform(CircleCrop())
                        .into(profile_picture_dialog)

                    Glide.with(requireActivity())
                        .load(selectedImageUri)
                        .transform(CircleCrop())
                        .into(profilePictureIn)

                }
            }
        }

    }

    //On Image Progress Listener
    override fun onProgressUpdate(percentage: Int) {
    }


    //Check if Internet is Good
    private fun checkInternet() {
        InternetCheck { internet ->
            //Internet is good
            if (internet)
                changeProfile()
            //Internet is bad
            else {

                progress_indicator_dialog.visibility = View.INVISIBLE
                dialogInternet.show(
                    fm,
                    "Internet check"
                )
                fm.executePendingTransactions();

                //onCancel or onDestroy InternetDialog
                dialogInternet.dialog!!.setOnCancelListener {
                    checkInternet()
                }


            }
        }
    }

}