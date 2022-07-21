package com.example.simmone.services

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import androidx.compose.Context
import com.example.simmone.R

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.M)
class SimmTileService : TileService() {
    private lateinit var cameraM: CameraManager
    var isFlash: Boolean = false


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick() {
        cameraM = getSystemService(Context.CAMERA_SERVICE)as CameraManager

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            flashLightOnOrOff()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun flashLightOnOrOff() {
        if(!isFlash){
            val cameraListId = cameraM.cameraIdList[0]
            cameraM.setTorchMode(cameraListId, true)
            isFlash = true
            changeTile(true)


        }
        else if (isFlash){
            val cameraListId = cameraM.cameraIdList[0]
            cameraM.setTorchMode(cameraListId,false)
            isFlash = false
            changeTile(false)
        }
    }


    override fun onTileRemoved() {

    }

    override fun onTileAdded() {


    }

    fun changeTile(isActive: Boolean) {
        val tile = qsTile

        if (isActive) {
            tile.state = Tile.STATE_ACTIVE
            tile.icon = Icon.createWithResource(this, R.drawable.simm_with_seed)
            tile.updateTile()
        }else {
            tile.state = Tile.STATE_INACTIVE
            tile.icon = Icon.createWithResource(this,R.drawable.simm_cover)
            tile.updateTile()

        }


    }

    override fun onStartListening() {
        super.onStartListening()

    }

    override fun onStopListening() {
        super.onStopListening()

    }
}