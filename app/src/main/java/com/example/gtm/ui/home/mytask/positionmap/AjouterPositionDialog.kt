package com.example.gtm.ui.home.mytask.positionmap


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gtm.data.entities.response.DataXX
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
class AjouterPositionDialog(
        name2: String,
        store2: DataXX
) :
    DialogFragment() {

    val name = name2
    val store = store2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_ajouter_position, container, false)
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
            AddPositionMapDialog(
                name,
                store
            ).show(requireActivity().supportFragmentManager, "AddPositionMapDialog")
            dialog!!.dismiss()
        }

        cancel_button.setOnClickListener {
            dialog!!.dismiss()
        }

    }


}

