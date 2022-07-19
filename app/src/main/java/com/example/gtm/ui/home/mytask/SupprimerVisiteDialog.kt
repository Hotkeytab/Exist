package com.example.gtm.ui.home.mytask


import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gtm.data.entities.response.mytaskplanning.supprimervisite.SuccessResponseDeleteVisite
import com.example.gtm.data.entities.response.mytaskplanning.getvisite.Visite
import com.example.gtm.utils.resources.Resource
import kotlinx.android.synthetic.main.dialog_delete_store.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SupprimerVisiteDialog(
    visiteId2: Int,
    positionVisite2: Int,
    taskAdapter2: TaskAdapter,
    items2: ArrayList<Visite>,
    items3: ArrayList<Visite>
) :
    DialogFragment() {

    val visiteId = visiteId2
    val positionVisite = positionVisite2
    private lateinit var response: Resource<SuccessResponseDeleteVisite>
    private val viewModel: MyTaskViewModel by viewModels()
    private val taskAdapter = taskAdapter2
    private val items = items2
    private val items4 = items3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_delete_store, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog!!.window!!.setWindowAnimations(R.style.AnimationsForMapDialogSwipe)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Accept to Delete Visite
        accept.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            accept.isEnabled = false
            cancel_button.isEnabled = false
            dialog!!.setCancelable(false)

            lifecycleScope.launch(Dispatchers.Main) {
                response = viewModel.deleteVisite(visiteId)

                if (response.responseCode == 202) {
                    progress_bar.visibility = View.GONE
                    items.removeAt(positionVisite)
                    items4.removeAt(positionVisite)
                    taskAdapter.notifyDataSetChanged()
                    dialog!!.dismiss()
                } else {
                    progress_bar.visibility = View.GONE
                    accept.isEnabled = true
                    cancel_button.isEnabled = true
                    dialog!!.setCancelable(true)

                }
            }


        }

        cancel_button.setOnClickListener {
            dialog!!.dismiss()
        }

    }


}

