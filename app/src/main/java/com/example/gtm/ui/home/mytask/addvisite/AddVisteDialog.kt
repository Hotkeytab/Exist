package com.example.gtm.ui.home.mytask.addvisite

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.BuildConfig
import com.example.gtm.data.entities.remote.VisitPost
import com.example.gtm.data.entities.response.*
import com.example.gtm.data.entities.ui.Image
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.MyTaskViewModel
import com.example.gtm.ui.home.mytask.TaskAdapter
import com.example.gtm.ui.home.mytask.survey.SurveyCheckDialog
import com.example.gtm.ui.home.mytask.survey.quiz.MyQuizViewModel
import com.example.gtm.utils.resources.Resource
import kotlinx.android.synthetic.main.dialog_add_visite.*
import kotlinx.android.synthetic.main.dialog_choix_image.*
import kotlinx.android.synthetic.main.dialog_choix_visitee.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.content.DialogInterface
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import android.text.Editable

import android.text.TextWatcher
import androidx.fragment.app.FragmentManager
import com.example.gtm.ui.home.mytask.positionmap.AddPositionMapDialog
import com.example.gtm.ui.home.mytask.positionmap.AjouterPositionDialog
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog
import com.example.gtm.utils.remote.Internet.InternetCheck
import com.example.gtm.utils.remote.Internet.InternetCheckDialog
import kotlinx.android.synthetic.main.dialog_edit_profile.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener


@AndroidEntryPoint
class AddVisteDialog(


) :
    DialogFragment(), AddVisiteAdapter.TaskItemListener {

    private val viewModel: AddVisiteDialogViewModel by viewModels()
    private val viewModelQuiz: MyQuizViewModel by viewModels()
    private lateinit var responseDataStores: Resource<GetStore>
    private lateinit var responseAdd: Resource<SuccessResponse>
    private lateinit var adapterAddVisite: AddVisiteAdapter
    private var listaDataXX = ArrayList<DataXX>()
    private var userId = 0
    lateinit var sharedPref: SharedPreferences
    private lateinit var dialogInternet: InternetCheckDialog
    private lateinit var fm: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_visite, container, false)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.65).toInt()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //  dialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog!!.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        userId = sharedPref.getInt("id", 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogInternet = InternetCheckDialog()
        fm = requireActivity().supportFragmentManager

        checkInternetGetStore()


        cancel.setOnClickListener {
            dialog!!.dismiss()
        }

        search_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

                if (search_text.text.toString().isEmpty()) {
                    adapterAddVisite.setItems(listaDataXX)
                } else {
                    val newArrayList = listaDataXX.filter { list ->
                        filterResearch(
                            list.name,
                            search_text.text.toString()
                        )
                    }
                    adapterAddVisite.setItems(newArrayList as ArrayList<DataXX>)
                }


            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })


        swiperefreshlayout.setOnRefreshListener(OnRefreshListener {

            checkInternetGetStore()
            swiperefreshlayout.isRefreshing = false
        })
    }

    override fun onClickedTask(
        taskId: Int,
        lat: Double?,
        lng: Double?,
        name: String,
        store: DataXX
    ) {

        if (lat == null || lng == null) {

            AjouterPositionDialog(name, store).show(
                requireActivity().supportFragmentManager,
                "AjouterPositionDialog"
            )


        } else {


            checkInternetAddVisite(taskId)


        }
    }


    private fun addVisite(taskId: Int) {
        dialog!!.setCancelable(false)
        cancel.isEnabled = false
        progress_indicator.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.Main) {
            val visitePost = VisitPost(null, getDateNow(), 0, taskId, userId, false, null)
            val arayListViste = ArrayList<VisitPost>()
            arayListViste.add(visitePost)
            responseAdd = viewModelQuiz.addVisite(arayListViste) as Resource<SuccessResponse>


            if (responseAdd.responseCode == 201) {
                dialog!!.setCancelable(true)
                cancel.isEnabled = true
                dialog!!.dismiss()

            } else {
                dialog!!.setCancelable(true)
                cancel.isEnabled = true
                progress_indicator.visibility = View.GONE
            }

        }
    }

    private fun getDateNow(): String {

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val d = Date()
        return sdf.format(d)

    }


    private fun getStores() {
        progress_indicator.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.Main) {


            responseDataStores = viewModel.getStores()

            if (responseDataStores.responseCode == 200) {
                progress_indicator.visibility = View.GONE
                listaDataXX = responseDataStores.data!!.data as ArrayList<DataXX>
                listaDataXX[0].lng = null
                listaDataXX.add(listaDataXX[0])
                listaDataXX.add(listaDataXX[0])
                listaDataXX.add(listaDataXX[0])

                setupRecycleViewPredictionDetail()
            } else {
                if (progress_indicator != null)
                    progress_indicator.visibility = View.GONE
            }

        }
    }


    private fun setupRecycleViewPredictionDetail() {

        adapterAddVisite = AddVisiteAdapter(this)
        getstorerecycle.isMotionEventSplittingEnabled = false
        getstorerecycle.layoutManager = LinearLayoutManager(requireContext())
        getstorerecycle.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        getstorerecycle.adapter = adapterAddVisite
        adapterAddVisite.setItems(listaDataXX)
        checkForResearch()

    }

    private fun checkForResearch() {
        if (listaDataXX.size > 3)
            search_bar.visibility = View.VISIBLE
    }


    private fun filterResearch(name: String, editTextName: String): Boolean {
        var patternRegex = ""
        var patternRegex2 = ""
        var count = 0
        var count2 = 0

        var editTextName2 = editTextName.replace('e', 'é')
        val chunks = editTextName2.toUpperCase().split("\\s+".toRegex())


        for (i in chunks) {
            if (count == 0) {
                patternRegex = "^($i.+)"
            } else {
                patternRegex += "\\s+($i.)"
            }
            count++
        }

        for (j in chunks) {
            if (count2 == 0) {
                patternRegex2 = "^($j+)"
            } else {
                patternRegex2 += "\\s+($j)"
            }
            count2++
        }


        /*  var patternRegex2 = "^(M.+)\\s+(G.)"
          Log.i("chunks", "$patternRegex2")
          Log.i("chunks", "$patternRegex")*/
        val regexFilter = Regex(patternRegex)
        val regexFilter2 = Regex(patternRegex2)

        return regexFilter.containsMatchIn(name.toUpperCase()) || regexFilter2.containsMatchIn(name.toUpperCase())
    }


    private fun checkInternetGetStore() {
        InternetCheck { internet ->
            if (internet)
                getStores()
            else {

              //  progress_indicator_dialog.visibility = View.INVISIBLE
                dialogInternet.show(
                    fm,
                    "Internet check"
                )
                fm.executePendingTransactions();

                dialogInternet.dialog!!.setOnCancelListener {
                    checkInternetGetStore()
                }


            }
        }
    }



    private fun checkInternetAddVisite(taskId: Int) {
        InternetCheck { internet ->
            if (internet)
                addVisite(taskId)
            else {

                progress_indicator_dialog.visibility = View.INVISIBLE
                dialogInternet.show(
                    fm,
                    "Internet check"
                )
                fm.executePendingTransactions();

                dialogInternet.dialog!!.setOnCancelListener {
                    checkInternetGetStore()
                }


            }
        }
    }


}



