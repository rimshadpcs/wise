package com.intractable.simm.utils

import android.text.TextUtils.split
import java.text.SimpleDateFormat
import java.util.*

object DateTimeFunctions {

    fun Date.toString(format: String = "yyyy/MM/dd HH:mm:ss", locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentHour(): Int{
        return getCurrentDateTimeString().split(" ")[1].split(":")[0].toInt()
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time  // gives the current time based on your time zone
    }

    fun getCurrentDateTimeString(): String {
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    fun getCurrentDateTimeEpochString(): String {
        return Calendar.getInstance().timeInMillis.toString()
    }

    fun getCurrentDateTimeEpoch(): Long {
        return Calendar.getInstance().timeInMillis
    }

    fun dateDifference(inputDate: String): Int{ // -1 is past day, 0 is same day, 1 is future day
        val currentDate = getCurrentDateTimeString().split(" ")[0].split("/")
        val currentYear = currentDate[0].toInt()
        val currentMonth = currentDate[1].toInt()
        val currentDay = currentDate[2].toInt()
        val inputDateString = inputDate.split(" ")[0].split("/")
        val inputYear = inputDateString[0].toInt()
        val inputMonth = inputDateString[1].toInt()
        val inputDay = inputDateString[2].toInt()

        if(inputYear > currentYear){
            return 1
        }
        else if (inputYear < currentYear){
            return -1
        }
        else{ // same year
            if(inputMonth > currentMonth){
                return 1
            }
            else if(inputMonth < currentMonth){
                return -1
            }
            else{
                if(inputDay > currentDay){
                    return 1
                }
                else if (inputDay < currentDay){
                    return -1
                }
                else{
                    return 0
                }
            }
        }
    }
}
