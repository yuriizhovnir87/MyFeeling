package com.yurazhovnir.myfeeling.ui.main.analytiic

import android.os.Bundle
import android.view.View
import com.yurazhovnir.myfeeling.base.BaseFragment
import com.yurazhovnir.myfeeling.databinding.FragmentAnalyticBinding
import com.yurazhovnir.myfeeling.ui.Screens

class AnalyticFragment : BaseFragment<FragmentAnalyticBinding>(FragmentAnalyticBinding::inflate) {
    override val TAG: String
        get() = "AnalyticFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickable = this
    }

    fun onStepsClick() {
        Screens.getAnalyticStepsFragment().let {
            add(android.R.id.content, it)
        }
    }
}