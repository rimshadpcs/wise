package com.rimapps.worldcup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException

class WordlCupViewModel() : ViewModel() {

    var worldcupList = MutableLiveData<ArrayList<Worldcup>>()


    fun generateCupList(){
        val worldcupList = ArrayList<Worldcup>()

        val wc1 = Worldcup("England","1")
        val wc2 = Worldcup("Germany","2")
        val wc3 = Worldcup("Franc","3")
        val wc4 = Worldcup("Spain","4")

        worldcupList.add(wc1)
        worldcupList.add(wc2)
        worldcupList.add(wc3)
        worldcupList.add(wc4)

        this.worldcupList.value = worldcupList

    }
}



