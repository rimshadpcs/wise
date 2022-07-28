package com.intractable.simm.viewmodel

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.intractable.simm.model.SwappingItems

class SwappingAppViewModel : ViewModel{

    constructor(): super()

    var swapList  = MutableLiveData<ArrayList<SwappingItems>>()

    fun generateSwapItems(){

        val swapList = ArrayList<SwappingItems>()

        val app1 = SwappingItems(Color.parseColor("#EE7A88"))
        val app2 = SwappingItems(Color.parseColor("#7AA8EE"))
        val app3 = SwappingItems(Color.parseColor("#DEEE7A"))

        swapList.add(app1)
        swapList.add(app2)
        swapList.add(app3)

        this.swapList.value = swapList



    }

}