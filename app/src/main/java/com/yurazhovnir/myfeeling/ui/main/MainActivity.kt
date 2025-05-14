package com.yurazhovnir.myfeeling.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import com.yurazhovnir.myfeeling.R
import com.yurazhovnir.myfeeling.base.BaseActivity
import com.yurazhovnir.myfeeling.base.ViewPagerAdapter
import com.yurazhovnir.myfeeling.databinding.ActivityMainBinding
import com.yurazhovnir.myfeeling.helper.AppDatabase
import com.yurazhovnir.myfeeling.ui.Screens

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewPager()
        setupBottomNavigation()
    }
    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager).apply {
            addFragment(Screens.getCalendarFragment())
            addFragment(Screens.getAnalyticFragment())
        }

        binding.viewPager?.apply {
            adapter = viewPagerAdapter
            offscreenPageLimit = 4
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {
                }

                override fun onPageSelected(position: Int) {
                    binding.bottomNavigationView.menu.getItem(position).isChecked = true
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }

    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.itemIconTintList = null
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.calendarNavItem -> binding.viewPager.currentItem = 0

                R.id.addNavItem -> {
                    Screens.getAddFilingFragment().let {
                        add(android.R.id.content, it)
                    }
                }

                R.id.analyticsNavItem -> {
                    binding.viewPager.currentItem = 2
                }
            }
            true
        }
    }

}