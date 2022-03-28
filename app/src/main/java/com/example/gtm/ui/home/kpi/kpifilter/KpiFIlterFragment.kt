package com.example.gtm.ui.home.kpi.kpifilter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.gtm.R
import com.example.gtm.databinding.FragmentKpiFIlterBinding
import com.example.gtm.databinding.FragmentKpiGraphBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KpiFIlterFragment : Fragment() {

    private lateinit var binding: FragmentKpiFIlterBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKpiFIlterBinding.inflate(inflater,container,false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      //  binding.topAppBar.title = "Analyse Kpi"


      /*  val items = listOf("Ariana", "Béja", "Ben Arous", "Bizerte", "Gabès", "Nabeul", "Jendouba", "Kairouan","Kasserine","Kebili","Kef","Mahdia","Manouba","Medenine","Monastir","Gafsa","Sfax","Sidi Bouzid","Siliana","Sousse","Tataouine","Tozeur","Zaghouan")

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        binding.villeText.setAdapter(arrayAdapter) */



    }


}