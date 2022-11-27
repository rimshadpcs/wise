package com.intractable.simm.services

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import android.hardware.camera2.CameraManager
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.Context
import com.intractable.simm.R
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.utils.Constants

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.M)
class SimmTileService : TileService() {
    private lateinit var cameraM: CameraManager
    private var isFlash: Boolean = false


    override fun onClick() {
        cameraM = getSystemService(Context.CAMERA_SERVICE)as CameraManager

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            flashLightOnOrOff()
        }
    }

    private fun flashLightOnOrOff() {
        if(!isFlash){
            val cameraListId = cameraM.cameraIdList[0]
            cameraM.setTorchMode(cameraListId, true)
            isFlash = true
            changeTile(true)
            Log.e("Flash","On")
            Config.instance.progressManager.updateState("case 2")
            //var isClicked: String = "CLICKED";




        }
        else if (isFlash){
            val cameraListId = cameraM.cameraIdList[0]
            cameraM.setTorchMode(cameraListId,false)
            isFlash = false
            changeTile(false)
            Constants.QSClicked = true
            Log.e("Flash","Off")
        }
    }


    override fun onTileRemoved() {

    }

    override fun onTileAdded() {
        Log.e("Tile","Added")
        Config.instance.progressManager.updateState("case 1")
    }

    private fun changeTile(isActive: Boolean) {
        val tile = qsTile

        if (isActive) {
            tile.state = Tile.STATE_ACTIVE
            tile.icon = Icon.createWithResource(this, R.drawable.qs_star)
            tile.updateTile()
        }else {
            tile.state = Tile.STATE_INACTIVE
            tile.icon = Icon.createWithResource(this, R.drawable.qs_star)
            tile.updateTile()

        }


    }


    override fun onStopListening() {
        super.onStopListening()

    }
}