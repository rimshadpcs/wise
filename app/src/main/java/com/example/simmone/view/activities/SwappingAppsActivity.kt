package com.example.simmone.view.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simmone.adapters.SwappingAppsAdapter
import com.example.simmone.databinding.ActivitySwappingAppsBinding
import com.example.simmone.model.SwappingItems
import com.example.simmone.utils.SwipeServices
import com.example.simmone.viewmodel.SwappingAppViewModel

class SwappingAppsActivity : AppCompatActivity(),
SwappingAppsAdapter.OnItemClickListener{
    private lateinit var swappingAppsBinding: ActivitySwappingAppsBinding
    private var swappingAppsAdapter: SwappingAppsAdapter? = null
    private lateinit var selectedApp: SwappingItems


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        swappingAppsBinding = ActivitySwappingAppsBinding.inflate(layoutInflater)
        setContentView(swappingAppsBinding.root)

        val swappingAppViewModel = ViewModelProvider(this)[SwappingAppViewModel::class.java]
        swappingAppViewModel.generateSwapItems()

        swappingAppViewModel.swapList.observe(this@SwappingAppsActivity) {
            swappingAppsAdapter = SwappingAppsAdapter(this@SwappingAppsActivity, it)
            swappingAppsBinding.rvSwappingApps.layoutManager = LinearLayoutManager(
                this@SwappingAppsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            swappingAppsBinding.rvSwappingApps.adapter = swappingAppsAdapter

        }

            val movingElement = swappingAppsBinding.rvSwappingApps
            val swipeServices = SwipeServices()

            //part where i call the swap service
            swipeServices.initialize(rootLayout =movingElement,movingElement,
                arrayListOf(SwipeServices.SwipeDirection.bottomToTop), 50)



    }

    override fun onItemClick(view: View, swappingItems: SwappingItems) {

        if (swappingAppsAdapter?.getItemList()?.indexOf(selectedApp)==2){

            val movingElement = swappingAppsBinding.rvSwappingApps

            SwipeServices().cancel()
        }

    }


}
