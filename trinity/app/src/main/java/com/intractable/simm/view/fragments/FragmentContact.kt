package com.intractable.simm.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.ContactsContract
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.intractable.simm.viewmodel.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentContact: FragmentOperation() {
    override val eventPrefix: String
        get() {
            return "simm_"+this.javaClass.simpleName
        }
    var flag =0
    private val viewModel: SessionViewModel by activityViewModels()

    val END_SESSION = 100
    override fun startData() {
        viewModel.startOperationData()
    }


    override fun doAction() {
        lifecycleScope.launch(Dispatchers.IO){
            delay(5000)
            if (flag==0) {
                checkForPermission(102)
                flag = 1
            }
        }

        if(flag==1){
            flag = 2
            val intent = Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI)
            startActivity(intent)
        }
        if (flag == 3){
            if (checkContact()) {
                flag = 4
                nextPage()
            }
            else {
                flag = END_SESSION
                changeTheText(viewModel.operationDataLiveData.value?.textError)
                showSkipButton()

            }
        }

    }

    override fun nextPage() {
        if (flag == END_SESSION){
            EndSession()
        }
        else
            viewModel.nextOperationPage()
    }


    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        if (flag == 2){
            nextPage()
            flag = 3
        }

    }

    @SuppressLint("Range", "Recycle")
    fun checkContact(): Boolean {

        val contentResolver = requireActivity().contentResolver
        val phones: Cursor? = contentResolver.query(
            ContactsContract.RawContacts.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
            arrayOf("Simm"),
            null
        )

        if (phones!!.moveToFirst()) // .getCount() > 0
            {
            return true
            }
        return false

    }

    private fun checkForPermission(requestCode: Int) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_CONTACTS
            ) -> {
                nextPage()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    android.Manifest.permission.READ_CONTACTS)
            }
        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                nextPage()
            } else {
                showSkipButton()
            }
        }



}