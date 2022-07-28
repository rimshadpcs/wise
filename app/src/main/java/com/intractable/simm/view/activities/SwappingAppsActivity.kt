package com.intractable.simm.view.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.intractable.simm.adapters.SwappingAppsAdapter
import com.intractable.simm.databinding.ActivitySwappingAppsBinding
import com.intractable.simm.model.SwappingItems
import com.intractable.simm.utils.SwipeServices
import com.intractable.simm.viewmodel.SwappingAppViewModel

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
