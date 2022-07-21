package com.example.simmone.view.activities
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.example.simmone.dataStore.GoldManager
import com.example.simmone.dataStore.dataStore
import com.example.simmone.databinding.ActivityMainBinding
import com.example.simmone.view.fragments.RightBottomSheetDialog
import com.example.simmone.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  lateinit var goldManager: GoldManager
    private lateinit var mainBinding: ActivityMainBinding
    private val mainModel : MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        goldManager = GoldManager(dataStore)
        observeGold()

        incrementReward(intent.getIntExtra("NextSession", 0))

        mainBinding.cvLaunch.setOnClickListener{
            val intent = Intent(this, SessionActivity::class.java)
            startActivity(intent)
        }

//        val modalBottomSheet = RightBottomSheetDialog()
//        modalBottomSheet.show(supportFragmentManager, RightBottomSheetDialog.TAG)
    }

    private fun incrementReward(nextSession : Int){
        if( nextSession==2) {
            saveGold()
            Toast.makeText(applicationContext,"reward", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeGold() {
        goldManager.goldCountFlow.asLiveData().observe(this) {
            if (it != null) {
                mainBinding.tvGold.text = it.toString()
            }
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun saveGold() {
        GlobalScope.launch {
            goldManager.storeGold()
        }
    }
}
