package com.intractable.simm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.intractable.simm.model.Statement

class StatementViewModel : ViewModel
{
    var statementDesc = ""
    var answer = ""

    constructor(): super()

    constructor(statement: Statement) : super()

    var mutableStatementList = MutableLiveData<ArrayList<StatementViewModel>>()
    var statementList = ArrayList<StatementViewModel>()

    var newMStatementList = MutableLiveData<ArrayList<Statement>>()
    fun generateStatement() {

        val newStatementList = ArrayList<Statement>()
        val statement1 = Statement("Lightning is seen before it’s heard because light travels faster than sound.","True")
        val statement2 = Statement("A coconut is a nut.","False")
        val statement3 = Statement("Tomatoes are fruit.","True")
        val statement4 = Statement("Mercury’s atmosphere is made up of Carbon Dioxide.","False")
        val statement5 = Statement("A snail can sleep up to 3 years.","True")
        val statement6 = Statement("Being scared of clouds is called Coulrophobia.","False")

        newStatementList.add(statement1)
        newStatementList.add(statement2)
        newStatementList.add(statement3)
        newStatementList.add(statement4)
        newStatementList.add(statement5)
        newStatementList.add(statement6)
        newMStatementList.value = newStatementList

    }

}




