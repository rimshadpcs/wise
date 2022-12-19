package com.rimapps.worldcup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rimapps.worldcup.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var worldCupAdapter: WorldCupAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val wordlCupViewModel = ViewModelProvider(this)[WordlCupViewModel::class.java]


        wordlCupViewModel.generateCupList()

        wordlCupViewModel.worldcupList.observe(this){
            worldCupAdapter = WorldCupAdapter(this@MainActivity,it)
            binding.rvCups.layoutManager = LinearLayoutManager(this)
            binding.rvCups.adapter = worldCupAdapter

        }
    }
}