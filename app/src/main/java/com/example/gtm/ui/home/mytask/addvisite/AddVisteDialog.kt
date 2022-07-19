package com.example.gtm.ui.home.mytask.addvisite

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.data.entities.remote.VisitPost
import com.example.gtm.ui.home.mytask.survey.quiz.MyQuizViewModel
import com.example.gtm.utils.resources.Resource
import kotlinx.android.synthetic.main.dialog_add_visite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.text.Editable

import android.text.TextWatcher
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.example.gtm.ui.home.mytask.positionmap.AjouterPositionDialog
import com.example.gtm.utils.remote.Internet.InternetCheck
import com.example.gtm.utils.remote.Internet.InternetCheckDialog
import kotlinx.android.synthetic.main.dialog_edit_profile.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.gtm.data.entities.response.mytaskplanning.ajoutervisite.GetStore
import com.example.gtm.data.entities.response.mytaskplanning.ajoutervisite.StoreServiceAjouterVisite
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.category.SuccessResponse
import com.example.gtm.data.entities.response.mytaskplanning.getvisite.Visite
import com.google.android.material.snackbar.Snackbar


@AndroidEntryPoint
class AddVisteDialog(
    var listaTasks2 : ArrayList<Visite>

) :
    DialogFragment(), AddVisiteAdapter.TaskItemListener {

    private val viewModel: AddVisiteDialogViewModel by viewModels()
    private val viewModelQuiz: MyQuizViewModel by viewModels()
    private lateinit var responseDataStores: Resource<GetStore>
    private lateinit var responseAdd: Resource<SuccessResponse>
    private lateinit var adapterAddVisite: AddVisiteAdapter
    private var listaDataXX = ArrayList<StoreServiceAjouterVisite>()
    private var userId = 0
    lateinit var sharedPref: SharedPreferences
    private lateinit var dialogInternet: InternetCheckDialog
    private lateinit var fm: FragmentManager
    private var listaTasks = listaTasks2


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
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

        //Get User ID From Shared Preferences
        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        userId = sharedPref.getInt("id", 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Init Dialog Internet Check Connection
        dialogInternet = InternetCheckDialog()
        fm = requireActivity().supportFragmentManager

        //Check Internet IF good then we call get Stores Service
        checkInternetGetStore()


        //Close dialog set on click listener
        cancel.setOnClickListener {
            dialog!!.dismiss()
        }


        //Advanced Search for Stores
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
                    adapterAddVisite.setItems(newArrayList as ArrayList<StoreServiceAjouterVisite>)
                }


            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })


        //Swipe Down For Refresh
        swiperefreshlayout.setOnRefreshListener(OnRefreshListener {
            checkInternetGetStore()
            swiperefreshlayout.isRefreshing = false
        })
    }



    //On Store Clicked
    override fun onClickedTask(
        taskId: Int,
        lat: Double?,
        lng: Double?,
        name: String,
        store: StoreServiceAjouterVisite
    ) {


        //If Store lat or Store lng is null then we have to fill position manually
        if (lat == null || lng == null) {

            AjouterPositionDialog(name, store).show(
                requireActivity().supportFragmentManager,
                "AjouterPositionDialog"
            )


        } else {


            //Repeat Add Visite service until internet is good
            checkInternetAddVisite(taskId)


        }
    }


    //Add Visite with New Store
    private fun addVisite(taskId: Int) {
        //Make DIalog Not canceable ( you cannot close it)
        dialog!!.setCancelable(false)
        cancel.isEnabled = false
        progress_indicator.visibility = View.VISIBLE

        //Launch Add Visite Couroutine
        GlobalScope.launch(Dispatchers.Main) {

            //Prepare visitePost Object
            val visitePost = VisitPost(null, getDateNow(), 0, taskId, userId, false, null)
            val arayListViste = ArrayList<VisitPost>()
            arayListViste.add(visitePost)

            //Get Response Service
            responseAdd = viewModelQuiz.addVisite(arayListViste) as Resource<SuccessResponse>


            //If Response is good
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



    //Get Current Date
    private fun getDateNow(): String {

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val d = Date()
        return sdf.format(d)

    }


    //Get All Stores
    private fun getStores() {
        //Set progress visible
        progress_indicator.visibility = View.VISIBLE

        //Launch couroutine
        lifecycleScope.launch(Dispatchers.Main) {

            //Get response from service
            responseDataStores = viewModel.getStores()

            //If Response is Good
            if (responseDataStores.responseCode == 200) {
                progress_indicator.visibility = View.GONE
                listaDataXX = responseDataStores.data!!.data as ArrayList<StoreServiceAjouterVisite>
                setupRecycleViewPredictionDetail()
            } else {
                if (progress_indicator != null)
                    progress_indicator.visibility = View.GONE
            }

        }
    }


    //Set RecycleView of all stores
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

    //If List of stores.size >3 then show the search bar
    private fun checkForResearch() {
        if (listaDataXX.size > 3)
            search_bar.visibility = View.VISIBLE
    }


    //Filter research algorithm
    private fun filterResearch(name: String, editTextName: String): Boolean {
        var patternRegex = ""
        var patternRegex2 = ""
        var count = 0
        var count2 = 0

        val editTextName2 = editTextName.replace('e', 'é')
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

        val regexFilter = Regex(patternRegex)
        val regexFilter2 = Regex(patternRegex2)

        return regexFilter.containsMatchIn(name.toUpperCase()) || regexFilter2.containsMatchIn(name.toUpperCase())
    }



    //Check for internet , if all good then getSTore service
    private fun checkInternetGetStore() {
        InternetCheck { internet ->
            //Internet is Good
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



    //Check for internet if all good then add visite with new store
    private fun checkInternetAddVisite(taskId: Int) {
        InternetCheck { internet ->
            if (internet) {

                var testId = false

                if(listaTasks.size > 0)
                {
                    for(i in listaTasks)
                    {
                       if(i.storeId == taskId)
                           testId = true
                    }
                }


                if(!testId)
                addVisite(taskId)
                else
                {
                    val snack = Snackbar.make(
                        requireView(),
                        "Ce magasin existe",
                        Snackbar.LENGTH_LONG
                    ).setBackgroundTint(resources.getColor(R.color.red))

                    val view: View = snack.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.CENTER
                    view.layoutParams = params
                    snack.show()
                }
            }
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



