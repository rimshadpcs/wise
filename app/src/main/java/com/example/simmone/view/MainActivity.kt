package com.example.simmone.view
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import com.example.simmone.dataStore.GoldManager
import com.example.simmone.dataStore.dataStore
import com.example.simmone.databinding.ActivityMainBinding
import com.example.simmone.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  lateinit var goldManager: GoldManager
    private lateinit var mainBinding: ActivityMainBinding
    private val mainModel : MainViewModel by viewModels()
    @Inject
    @Named("continueButton")
    lateinit var continueButton: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        goldManager = GoldManager(dataStore)
        observeGold()

        incrementReward(intent.getIntExtra("NextSession", 0))

        mainBinding.btStart.text = continueButton
        mainBinding.btStart.setOnClickListener{
            val intent = Intent(this, BubbleSample::class.java)
            startActivity(intent)
        }
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
