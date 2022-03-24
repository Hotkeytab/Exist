package com.example.gtm.ui.home.mytask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gtm.databinding.BeforeHomeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BeforeHomeFragment : Fragment() {


    private lateinit var binding: BeforeHomeBinding
    override fun onStart() {
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BeforeHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }








}

