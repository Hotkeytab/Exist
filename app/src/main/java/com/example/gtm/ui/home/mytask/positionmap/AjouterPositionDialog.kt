package com.example.gtm.ui.home.mytask.positionmap


import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import android.view.*
import com.example.gtm.data.entities.response.mytaskplanning.ajoutervisite.StoreServiceAjouterVisite
import kotlinx.android.synthetic.main.dialog_delete_store.*


@AndroidEntryPoint
class AjouterPositionDialog(
        name2: String,
        store2: StoreServiceAjouterVisite
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

