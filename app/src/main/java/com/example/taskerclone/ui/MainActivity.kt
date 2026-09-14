package com.example.taskerclone.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.taskerclone.R
import com.example.taskerclone.data.AppDatabase
import com.example.taskerclone.data.ProfileEntity
import com.example.taskerclone.databinding.ActivityMainBinding
import com.example.taskerclone.service.AutomationService
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAutomationService()
        loadProfiles()

        binding.btnAddProfile.setOnClickListener { addProfileFromForm() }
    }

    private fun startAutomationService() {
        val serviceIntent = Intent(this, AutomationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun loadProfiles() {
        val dao = AppDatabase.getInstance(applicationContext).profileDao()
        lifecycleScope.launch {
            dao.getAll().collect { profiles ->
                val display = profiles.map {
                    "${it.name} | ${it.triggerType}(${it.triggerValue}) -> ${it.actionType}(${it.actionValue}) [${if (it.enabled) "ON" else "OFF"}]"
                }
                binding.listProfiles.adapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_list_item_1,
                    display
                )
            }
        }
    }

    private fun addProfileFromForm() {
        val name = binding.inputName.text.toString().ifBlank { "Untitled" }
        val triggerType = binding.inputTriggerType.text.toString().ifBlank { "TIME" }
        val triggerValue = binding.inputTriggerValue.text.toString()
        val actionType = binding.inputActionType.text.toString().ifBlank { "NOTIFY" }
        val actionValue = binding.inputActionValue.text.toString()

        val profile = ProfileEntity(
            name = name,
            triggerType = triggerType,
            triggerValue = triggerValue,
            actionType = actionType,
            actionValue = actionValue,
            enabled = true
        )

        val dao = AppDatabase.getInstance(applicationContext).profileDao()
        lifecycleScope.launch {
            dao.insert(profile)
        }

        binding.inputName.text.clear()
        binding.inputTriggerValue.text.clear()
        binding.inputActionValue.text.clear()
    }
}
