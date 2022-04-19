package com.example.gtm.ui.home.mytask.survey.question

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.data.entities.ui.Image
import kotlinx.android.synthetic.main.dialog_afficher_image.*


@AndroidEntryPoint
class AfficherImageDialog(
    position2: Int,
    listaImage2: HashMap<Int,ArrayList<Image>>,
    linearImage2: LinearLayout,
    plus_image2: LinearLayout,
    adapterImage2: ImageAdapter,
    recycle_view2: RecyclerView,
    i2: Int

) :
    DialogFragment() {


    private var position = position2
    private val listaImage = listaImage2
    private val linearImage = linearImage2
    private val plus_image = plus_image2
    private val adapterImage = adapterImage2
    private val recycle_view = recycle_view2
    private val i = i2


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_afficher_image, container, false)
    }

    override fun onStart() {
        super.onStart()

        val width = (resources.displayMetrics.widthPixels).toInt()
        val height = (resources.displayMetrics.heightPixels).toInt()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window?.setLayout(width, height)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        afficher_image.setImageBitmap(listaImage[i]!![position].url)

        return_from_dialog.setOnClickListener {
            dismiss()
        }

        left_arrow.setOnClickListener {
            if (position != 0 && listaImage[i]!!.size != 1) {
                position--
                afficher_image.setImageBitmap(listaImage[i]!![position].url)
            }
        }

        right_arrow.setOnClickListener {
            if (listaImage[i]!!.size - 1 != position && listaImage[i]!!.size != 1) {
                position++
                afficher_image.setImageBitmap(listaImage[i]!![position].url)
            }

        }

        delete.setOnClickListener {
            if (listaImage[i]!!.size == 1) {
                listaImage[i]!!.removeAt(0)
                linearImage.visibility = View.VISIBLE
                plus_image.visibility = View.GONE
                adapterImage.setItems(listaImage[i]!!)
                //recycle_view.visibility = View.GONE
                dismiss()

            } else if (listaImage[i]!!.size > 1) {
                if (position == 0) {

                    listaImage[i]!!.removeAt(0)
                    afficher_image.setImageBitmap(listaImage[i]!![position].url)
                    adapterImage.setItems(listaImage[i]!!)

                } else if (position == listaImage[i]!!.size - 1) {

                    listaImage[i]!!.removeAt(listaImage[i]!!.size - 1)
                    position --
                    afficher_image.setImageBitmap(listaImage[i]!![position].url)
                    adapterImage.setItems(listaImage[i]!!)
                } else {
                    listaImage[i]!!.removeAt(position)
                    afficher_image.setImageBitmap(listaImage[i]!![position].url)
                    adapterImage.setItems(listaImage[i]!!)
                }
            } else
                dismiss()
        }

    }


}



