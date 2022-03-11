package com.example.gtm.ui.home.mytask


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gtm.data.entities.response.SuccessResponseWithMessage
import com.example.gtm.data.entities.response.TimeClass
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.utils.resources.Resource
import kotlinx.android.synthetic.main.dialog_delete_store.*
import kotlinx.android.synthetic.main.dialog_delete_store.progress_indicator
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SupprimerVisiteDialog(
    visiteId2: Int,
    positionVisite2: Int,
    taskAdapter2: TaskAdapter,
    items2: ArrayList<Visite>,
    items3 : ArrayList<Visite>
) :
    DialogFragment() {

    val visiteId = visiteId2
    val positionVisite = positionVisite2
    private lateinit var response: Resource<SuccessResponseWithMessage>
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

        val width = (resources.displayMetrics.widthPixels * 0.99).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.88).toInt()
        dialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog!!.window!!.setWindowAnimations(R.style.AnimationsForMapDialogSwipe)
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accept.setOnClickListener {
            progress_bar.visibility = View.VISIBLE


            /*  items.removeAt(positionVisite)
              taskAdapter.notifyDataSetChanged()
              taskAdapter.notifyItemRemoved(positionVisite)*/


            lifecycleScope.launch(Dispatchers.Main) {
                response = viewModel.deleteVisite(visiteId)

                if (response.responseCode == 202) {
                    progress_bar.visibility = View.GONE
                    Log.i("alehhakka","${items.size}")
                    items.removeAt(positionVisite)
                    items4.removeAt(positionVisite)
                   // taskAdapter.notifyItemRemoved(positionVisite)
                    taskAdapter.notifyDataSetChanged()
                    dialog!!.dismiss()
                    Log.i("alehhakka","${items.size}")
                } else {
                    progress_bar.visibility = View.GONE

                }
            }


            Log.i("idandposition", "$visiteId and $positionVisite")


        }

        cancel_button.setOnClickListener {
            dialog!!.dismiss()
        }

    }


}

