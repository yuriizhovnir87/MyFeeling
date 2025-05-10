package com.yurazhovnir.myfeeling.ui

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.lifecycleScope
import com.yurazhovnir.myfeeling.R
import com.yurazhovnir.myfeeling.base.BaseFragment
import com.yurazhovnir.myfeeling.model.Filing
import com.yurazhovnir.myfeeling.helper.HealthConnectManager
import com.yurazhovnir.myfeeling.PERMISSIONS
import com.yurazhovnir.myfeeling.databinding.FragmentAddFilingBinding
import com.yurazhovnir.myfeeling.helper.DatabaseChangeListener
import com.yurazhovnir.myfeeling.helper.DatabaseHelper
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class AddFilingFragment : BaseFragment<FragmentAddFilingBinding>(FragmentAddFilingBinding::inflate), DatabaseChangeListener {
    override val TAG: String
        get() = "AddFilingFragment"
    private var emoji: String = ""
    private lateinit var healthConnectManager: HealthConnectManager
    private lateinit var databaseHelper: DatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickable = this
        databaseHelper = DatabaseHelper(requireContext())
        databaseHelper.addDatabaseChangeListener(this)

        healthConnectManager = HealthConnectManager(requireContext())

        setupEmojiSelector()
    }

    fun onSaveClick() {
        lifecycleScope.launch {
            val newFil = Filing()
            val current = LocalDateTime.now()
            newFil.startsAt = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            newFil.dueAt = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            newFil.id = Date().time.toInt()
            newFil.comment = binding.commentView.text?.toString()
            newFil.emoji = emoji
            if (healthConnectManager.checkAndRequestPermissions()) {
                val summary = healthConnectManager.getDailyBiometricSummary()
                newFil.steps = summary.steps
                newFil.hydration = summary.hydration
                newFil.sleep = summary.sleepHours?.toInt()
                val dbHelper = DatabaseHelper(requireContext())
                dbHelper.insertFiling(newFil)
//                activity?.onBackPressed()
            } else {
                val dbHelper = DatabaseHelper(requireContext())
                dbHelper.insertFiling(newFil)
//                activity?.onBackPressed()
            }
        }
    }

    private fun setupEmojiSelector() {
        val emojiContainer = binding.emojiContainer
        val emojis = listOf("😄", "😊", "😐", "😢", "😡")
        val emojiViews = mutableListOf<TextView>()

        emojis.forEach { emoji ->
            val emojiView = TextView(requireContext()).apply {
                text = emoji
                textSize = 40f
                gravity = Gravity.CENTER
                setTextColor(ContextCompat.getColor(requireContext(), R.color.base_black))
                setPadding(0, 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = resources.getDimensionPixelSize(R.dimen.emoji_margin)
                    topMargin = resources.getDimensionPixelSize(R.dimen.emoji_top_margin)
                }
                background = ContextCompat.getDrawable(requireContext(), R.drawable.emoji_selector_background)

                setOnClickListener {
                    emojiViews.forEach { view -> view.isSelected = false }

                    isSelected = true
                    onEmojiSelected(emoji)
                }
            }

            emojiViews.add(emojiView)
            emojiContainer.addView(emojiView)
        }
    }
    fun onConnectToGoogleHealthClick(){
        val healthConnectClient = HealthConnectClient.getOrCreate(requireContext())
        lifecycleScope.launch {
            checkPermissionsAndRun(healthConnectClient)
        }
    }

    private fun onEmojiSelected(emoji: String) {
        this@AddFilingFragment.emoji = emoji
    }
    private suspend fun checkPermissionsAndRun(healthConnectClient: HealthConnectClient) {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (granted.any { it in PERMISSIONS }) {
            binding.googleHealthView.isVisible = false
        } else {
            requestPermissions.launch(PERMISSIONS)
        }
    }

    private val requestPermissions = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.any { it in PERMISSIONS }) {
            lifecycleScope.launch {
                binding.googleHealthView.isVisible = false
            }
        }
    }

    override fun onDataChanged() {
      val ttest  = databaseHelper.getAllFilings()
        ttest
    }
}