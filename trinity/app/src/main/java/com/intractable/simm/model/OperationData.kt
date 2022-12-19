package com.intractable.simm.model

data class OperationData(val text: String, val color: String, val shouldDoAction: Boolean, val shouldGoToNextActivity: Boolean,
                         val img:Int, var hasCountDown: Boolean,var sessionType:Int=0,var textError:String="")
