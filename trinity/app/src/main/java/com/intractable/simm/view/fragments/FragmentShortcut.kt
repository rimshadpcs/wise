package com.intractable.simm.view.fragments

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import com.intractable.simm.R
import com.intractable.simm.view.activities.LockOrUnlockActivity
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.view.activities.SettingsActivity
import com.intractable.simm.viewmodel.SessionViewModel
import java.util.ArrayList


class FragmentShortcut:FragmentOperation() {
    private val viewModel: SessionViewModel by activityViewModels()
    var shortcutManager:ShortcutManager? = null

    private var RETRY_FLAG = 0
    override fun startData() {
        viewModel.startOperationData()
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun doAction() {
        setShortcut()
    }

    override fun nextPage() {
        viewModel.nextOperationPage()
    }


    override val eventPrefix: String
        get() {
            return "simm_"+this.javaClass.simpleName
        }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun setShortcut(){

        shortcutManager = context?.getSystemService(ShortcutManager::class.java)
        var intent1 = Intent(context, SessionActivity::class.java)
            intent1.action = Intent.ACTION_VIEW

        val shortcutInfo1 = ShortcutInfo.Builder(context, "ID1")
            .setShortLabel("Session page")
            .setLongLabel("Go to sessions")
            .setIcon(Icon.createWithResource(context, R.drawable.simm_image))
            .setIntent(intent1)
            .build()

        val shortcutInfoList: MutableList<ShortcutInfo> = ArrayList()
        shortcutInfoList.add(shortcutInfo1)

        shortcutManager?.dynamicShortcuts = shortcutInfoList
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun UpdateShortCut(){
        var intent1 = Intent(context, LockOrUnlockActivity::class.java)
        intent1.action = Intent.ACTION_VIEW
        intent1.putExtra("shortcut",1)

        val shortcutInfo = ShortcutInfo.Builder(context, "ID2")
            .setShortLabel("Session page")
            .setLongLabel("Go to sessions")
            .setIcon(Icon.createWithResource(context, R.drawable.simm_image))
            .setIntent(intent1)
            .build()

        val shortcutInfoList: MutableList<ShortcutInfo> = ArrayList()
        shortcutInfoList.add(shortcutInfo)

        shortcutManager?.addDynamicShortcuts(shortcutInfoList)

    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun onDestroy() {
        super.onDestroy()
        shortcutManager?.removeDynamicShortcuts(listOf("ID1"))
       UpdateShortCut()

    }
}